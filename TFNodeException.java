package termproject;

/**
 * Title:        Term Project 2-4 Trees
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author      David Tarwater and Jadon Spears
 * @version 1.0
 */

public class TFNodeException extends RuntimeException {

    public TFNodeException() {
        super ("Problem with TFNode");
    }
    public TFNodeException(String errorMsg) {
        super (errorMsg);
    }
}
