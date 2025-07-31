package org.betonquest.betonquest.api.quest.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;

public interface NewObjData {
    interface Factory {

        Factory DEFAULT = Stolen::new;

        NewObjData create(final String instruction, final Profile profile, final String objID) throws QuestException;
    }

    class Stolen extends Objective.ObjectiveData implements NewObjData {

        public Stolen(final String instruction, final Profile profile, final String objID) {
            super(instruction, profile, objID);
        }
    }
}
