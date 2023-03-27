package actions;

import fileio.ActionInputData;

/**
 * Clasa abstracta care reprezinta o actiune
 *
 * Va fi mostenita de alte clase care vor specifica tipul actiunii
 * si vor implementa operatiile specifice fiecarui tip de actiune
 */
public abstract class Action {
    /**
     * Parametrii care sunt siguri comuni tuturor actiunilor
     */
    protected final int actionId;
    protected final String type;

    public Action(final ActionInputData input) {
        actionId = input.getActionId();
        type = input.getType();
    }

    /**
     * Metoda care ruleaza actiunea asupra bazei de date si
     * intoarce rezultatul actiunii sub forma de String
     * @return Rezultatul actiunii
     */
    public abstract String runAction();
}
