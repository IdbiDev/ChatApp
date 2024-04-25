package me.idbi.chatapp.messages;

import java.util.ArrayList;
import java.util.List;

public class MessageUtils {

    /*
    import mouse
from blessed import Terminal

term = Terminal()
messages = [str(i) for i in range(400)]
scrollstate = 0
msgs_per_scroll = 3

def on_scroll(event):
    global scrollstate
    if isinstance(event,mouse.WheelEvent):
        scrollstate += event.delta
        get_scroll_messages()


def get_scroll_messages() -> list[str]:
    global scrollstate, messages
    scrollstate = max(scrollstate, 0)
    idx1 = (len(messages)) - scrollstate * msgs_per_scroll
    idx2 = (len(messages)) - scrollstate * msgs_per_scroll - term.height
    scroll_msg = messages[int(max(idx2, 0)):int(max(idx1, term.height - 1))]
    return scroll_msg


def start_listener():
    mouse.hook(on_scroll)
    while True:
        pass

# start_listener()
scrollstate = 122
get_scroll_messages()
     */

    private static int messagesPerScroll = 3;
    public static List<String> getScrollMessages() {
        List<String> allMessages = new ArrayList<String>();
        List<String> messages = new ArrayList<String>();
        int scrollState = 1;

        int idx1 = allMessages.size() - scrollState * messagesPerScroll;
        int idx2 = allMessages.size() - scrollState * messagesPerScroll - 120; // - term.height

        return allMessages.subList(Math.max(idx2, 0), Math.max(idx1, 120 - 1));
    }
}
