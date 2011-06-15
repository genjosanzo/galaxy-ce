package org.mule.galaxy.web.client.ui.field;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Store;

/**
 * Generic composite {@link SearchStoreFilterField} implementation.
 * <br />
 * Filter criterion are abstracted as {@link Criteria}.
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

        protected <T extends ModelData> String extractValue(final T record) {
            final V propertyValue = record.<V>get(this.property);
            if (propertyValue == null) {
                return null;
            }
            final String convertedValue = convertValue(propertyValue);
            if (convertedValue == null) {
                return null;
            }
            return convertedValue;
        }

        protected String convertValue(final V propertyValue) {
            return propertyValue.toString();
        }

    }

    private static final String CRITERIA_SEPARATOR = " ";
    private static final String PROPERTY_SEPARATOR = ":";

    private final Criteria<?>[] criteria;

    public CompositeSearchStoreFilterField(final Criteria<?> ... criteria) {
        this.criteria = criteria;

        setToolTip(createToolTip(criteria));
    }

   protected String createToolTip(final Criteria<?> ... criteria) {
       final StringBuilder builder = new StringBuilder("Filter by ");
       for (int i = 0; i < criteria.length; i++) {
           final Criteria<?> criterion = criteria[i];
           builder.append(criterion.getPrefix());
           if (i == criteria.length-2) {
               builder.append(" or ");
           } else if (i != criteria.length-1) {
               builder.append(", ");
           }
       }
       builder.append(".");
       return builder.toString();
   }

    protected final Criteria<?> findCriterion(final String property) {
        for (final Criteria<?> criterion : this.criteria) {
            if (criterion.getPrefix().equals(property)) {
                return criterion;
            }
        }
        return null;
    }

    protected final boolean matches(final M record, final String prefix, final String filter) {
        final Criteria<?> criterion = findCriterion(prefix);
        if (criterion != null) {
            return matches(record, criterion, filter);
        }
        //Ignore filter with non matching criterion
        return false;
    }

    protected final boolean matches(final M record, final String filter) {
        for (final Criteria<?> criterion : this.criteria) {
            final boolean matches = matches(record, criterion, filter);
            if (matches) {
                return true;
            }
        }
        return false;
    }

    protected final boolean matches(final M record, final Criteria<?> criterion, final String filter) {
        final String value = criterion.extractValue(record);
        //TODO regex matching ?
        return value != null && value.contains(filter);
    }

    @Override
    protected final boolean doSelect(final Store<M> store, final M parent, final M record, final String property, final String filter) {
        final String[] normalizedFilters = filter.split(CompositeSearchStoreFilterField.CRITERIA_SEPARATOR);
        for (final String normalizedFilter : normalizedFilters) {
            final boolean matches;
            if (normalizedFilter.contains(CompositeSearchStoreFilterField.PROPERTY_SEPARATOR)) {
                //Filter contains a prefix.
                //Apply to a single criterion.
                final String[] prefixedNormalizedFilter = normalizedFilter.split(CompositeSearchStoreFilterField.PROPERTY_SEPARATOR);
                if (prefixedNormalizedFilter.length != 2) {
                    //No value provided yet, skip
                    return true;
                }
                matches = matches(record, prefixedNormalizedFilter[0], prefixedNormalizedFilter[1]);
            } else {
                //Filter does not contain a prefix.
                //Applies to all criterion.
                matches = matches(record, normalizedFilter);
            }
            //If one of filter does not match then do not select this record
            if (!matches) {
                return false;
            }
        }
        //All criterion match.
        return true;
    }

}
