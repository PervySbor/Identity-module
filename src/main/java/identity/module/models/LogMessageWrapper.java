package identity.module.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import identity.module.enums.LogType;

public class LogMessageWrapper {

    private final LogMessage logMessage;
    private final LogType index;

    public LogMessageWrapper(LogMessage logMessage, LogType index){
        this.logMessage = logMessage;
        this.index = index;
    }

    @JsonGetter
    public LogMessage getLogMessage() {
        return logMessage;
    }

    @JsonGetter
    public String getIndex() {
        return this.index.name();
    }
}
