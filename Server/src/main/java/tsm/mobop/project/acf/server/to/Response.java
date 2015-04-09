package tsm.mobop.project.acf.server.to;

import tsm.mobop.project.acf.server.ro.RemoteObject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Response {

    private String message;
    private boolean success;
    private RemoteObject data;

    public Response(String message, boolean success, RemoteObject data){
        this.message = message;
        this.success = success;
        this.data = data;
    }

    @XmlElement(name = "message")
    public String getMessage() {
        return message;
    }

    @XmlElement(name = "success")
    public boolean isSuccess() {
        return success;
    }

    @XmlElement(name = "data")
    public RemoteObject getData() {
        return data;
    }
}
