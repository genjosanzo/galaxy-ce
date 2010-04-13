package org.mule.galaxy.repository.client.util;

import com.google.gwt.user.client.ui.SuggestOracle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mule.galaxy.repository.rpc.ItemInfo;
import org.mule.galaxy.repository.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.client.ui.panel.ErrorPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;

public class ItemPathOracle extends SuggestOracle {

    private ErrorPanel errorPanel;
    private final String exclude;
    private final String[] types;
    private boolean recursive;
    private final RegistryServiceAsync registryService;
    
    public ItemPathOracle(RegistryServiceAsync registryService, ErrorPanel errorPanel, boolean recursive, String exclude, String... types) {
        super();
        this.registryService = registryService;    
        this.errorPanel = errorPanel;
        this.recursive = recursive;
        this.exclude = exclude;
        this.types = types;
    }

    public ItemPathOracle(RegistryServiceAsync registryService, ErrorPanel errorPanel) {
        this(registryService, errorPanel, true, "xxx", new String[0]);
    }


    @Override
    public void requestSuggestions(final Request request, final Callback callback) {
        registryService.suggestItems(request.getQuery(), recursive, exclude,
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
