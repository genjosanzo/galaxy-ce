package org.mule.galaxy.web.client.ui.field;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Store;

/**
 *
 * Generic composite {@link SearchStoreFilterField} implementation.
 * <br />
 * Filter criterions are abstracted as {@link Criteria}.
 *
 * @param <M>
 */
public class CompositeSearchStoreFilterField<M extends ModelData> extends SearchStoreFilterField<M> {

    public static class Criteria<V> {

        private final String property;
        private final String prefix;

        public Criteria(final String property) {
            this(property, property);
        }

        public Criteria(final String property, final String prefix) {
            if (property == null) {
                throw new IllegalArgumentException("null property");
            }
            if (prefix == null) {
                throw new IllegalArgumentException("null prefix");
            }

            this.property = property;
            this.prefix = prefix;
        }

        public final String getProperty() {
            return this.property;
        }

        public final String getPrefix() {
            return this.prefix;
        }

        protected final <T extends ModelData> String extractValue(final T record) {
            final V propertyValue = record.get(this.property);
            if (propertyValue == null) {
                return null;
            }
            final String convertedValue = convertValue(propertyValue);
            if (convertedValue == null) {
                return null;
            }
            return convertedValue.toLowerCase();
        }

        protected String convertValue(final V propertyValue) {
            return propertyValue.toString();
        }

    }

    private static final String CRITERIA_SEPARATOR = " ";
    private static final String PROPERTY_SEPARATOR = ":";

    private final Criteria<?>[] criterias;

    public CompositeSearchStoreFilterField(final Criteria<?> ... criterias) {
        this.criterias = criterias;
    }

    protected final Criteria<?> findCriteria(final String property) {
        for (final Criteria<?> criteria : this.criterias) {
            if (criteria.getPrefix().equals(property)) {
                return criteria;
            }
        }
        return null;
    }

    protected final boolean matches(final M record, final String prefix, final String filter) {
        final Criteria<?> criteria = findCriteria(prefix);
        if (criteria != null) {
            return matches(record, criteria, filter);
        }
        //Ignore filter with non matching criteria
        return true;
    }

    protected final boolean matches(final M record, final String filter) {
        for (final Criteria<?> criteria : this.criterias) {
            final boolean matches = matches(record, criteria, filter);
            if (matches) {
                return true;
            }
        }
        return false;
    }

    protected final boolean matches(final M record, final Criteria<?> criteria, final String filter) {
        final String value = criteria.extractValue(record);
        return value != null && value.contains(filter);
    }

    @Override
    protected final boolean doSelect(final Store<M> store, final M parent, final M record, final String property, final String filter) {
        final String[] normalizedFilters = filter.toLowerCase().split(CompositeSearchStoreFilterField.CRITERIA_SEPARATOR);
        for (final String normalizedFilter : normalizedFilters) {
            final boolean matches;
            if (normalizedFilter.contains(CompositeSearchStoreFilterField.PROPERTY_SEPARATOR)) {
                //Filter contains a prefix.
                //Apply to a single criteria.
                final String[] prefixedNormalizedFilter = normalizedFilter.split(CompositeSearchStoreFilterField.PROPERTY_SEPARATOR);
                matches = matches(record, prefixedNormalizedFilter[0], prefixedNormalizedFilter[1]);
            } else {
                //Filter does not contain a prefix.
                //Applies to all criterias.
                matches = matches(record, normalizedFilter);
            }
            //If one of filter does not match then do not select this record
            if (!matches) {
                return false;
            }
        }
        //All criterias match.
        return true;
    }

}
