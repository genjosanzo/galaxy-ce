package org.mule.galaxy.web.client.util;

import com.google.gwt.user.client.ui.SuggestOracle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemInfo;

public class ItemPathOracle extends SuggestOracle {

    private Galaxy galaxy;
    private ErrorPanel errorPanel;
    private final String exclude;
    private final String[] types;
    private boolean recursive;
    
    public ItemPathOracle(Galaxy galaxy, ErrorPanel errorPanel, boolean recursive, String exclude, String... types) {
        super();    
        this.errorPanel = errorPanel;
        this.galaxy = galaxy;
        this.recursive = recursive;
        this.exclude = exclude;
        this.types = types;
    }

    public ItemPathOracle(Galaxy galaxy, ErrorPanel errorPanel) {
        this(galaxy, errorPanel, true, "xxx", new String[0]);
    }


    @Override
    public void requestSuggestions(final Request request, final Callback callback) {
        galaxy.getRegistryService().suggestItems(request.getQuery(), recursive, exclude,
            types, new AbstractCallback<Collection<ItemInfo>>(errorPanel) {

            public void onSuccess(Collection<ItemInfo> entries) {
                updateSuggestions(entries, request, callback);
            }
            
        });
    }

    protected void updateSuggestions(Collection<ItemInfo> entries, Request request, Callback callback) {
        Response response = new Response();
        List<Suggestion> suggestions = new ArrayList<Suggestion>();
        for (final ItemInfo e : entries) {
            suggestions.add(new Suggestion() {
                public String getDisplayString() {
                    return e.getPath();
                }

                public String getReplacementString() {
                    return e.getPath();
                }
            });
        }
        if (suggestions.size() == 0) {
            suggestions.add(new Suggestion() {
                public String getDisplayString() {
                    return "<i>No results found</i>";
                }

                public String getReplacementString() {
                    return null;
                }
            });
        }
        response.setSuggestions(suggestions);
        callback.onSuggestionsReady(request, response);
    }

    @Override
    public boolean isDisplayStringHTML() {
        return true;
    }

}
