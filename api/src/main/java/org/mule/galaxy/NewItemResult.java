package org.mule.galaxy;

import java.util.List;
import java.util.Map;

import org.mule.galaxy.policy.ApprovalMessage;

public class NewItemResult {
    private Item item;
    private Map<Item, List<ApprovalMessage>> approvals;
    
    public NewItemResult(Item item, Map<Item, List<ApprovalMessage>> approvals) {
        super();
        this.item = item;
        this.approvals = approvals;
    }

    public Item getItem() {
        return item;
    }

    public Map<Item, List<ApprovalMessage>> getApprovals() {
        return approvals;
    }

}
