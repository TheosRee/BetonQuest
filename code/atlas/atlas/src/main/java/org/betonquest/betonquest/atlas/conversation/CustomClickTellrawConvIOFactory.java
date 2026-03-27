package org.betonquest.betonquest.atlas.conversation;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationIOFactory;

/**
 * Tellraw conversation output.
 */
public class CustomClickTellrawConvIOFactory implements ConversationIOFactory {

    /**
     * The colors used for the conversation.
     */
    private final ConversationColors colors;

    /**
     * Create a new Tellraw conversation IO factory.
     *
     * @param colors the colors used for the conversation
     */
    public CustomClickTellrawConvIOFactory(final ConversationColors colors) {
        this.colors = colors;
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) {
        return new CustomClickTellrawConvIO(conversation, onlineProfile, colors);
    }
}
