/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.web.client.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * A chain of multiple validators. This is a fail-fast validator, meaning it will
 * break the chain and stop further processing on encountering the first validation failure.
 * <p/>
 * Instances of this class are NOT thread-safe.
 */
public class ChainedValidator implements Validator {

    protected List<Validator> validatorChain = new ArrayList<Validator>();
    protected Validator lastChecked;

    /**
     * TODO this method should really be a varargs one instead, upgrade
     * once GWT 1.5 is used in Galaxy.
     */
    public ChainedValidator(final List<Validator> validatorChain) {
        if (validatorChain == null || validatorChain.isEmpty()) {
            return;
        }
        this.validatorChain.addAll(validatorChain);
    }

    public ChainedValidator(final Validator... validators) {
        for (Validator v : validators)
            validatorChain.add(v);
    }


    public boolean validate(final Object value) {
        // reset the last checked validator first
        lastChecked = null;

        for (int i = 0; i < validatorChain.size(); i++) {
            lastChecked = validatorChain.get(i);
            if (!lastChecked.validate(value)) {
                return false;
            }
        }

        return true;
    }

    public String getFailureMessage() {
        return lastChecked.getFailureMessage();
    }


}