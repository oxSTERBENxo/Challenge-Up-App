package org.example;

import java.util.ArrayList;
import java.util.List;

public class Response<T> {
    private List<T> Data;

    public Response() {
        Data = new ArrayList<T>();
    }

    public Response(List<T> data) {
        this.Data = data;
    }

    public List<T> getData() {
        return Data;
    }
    public void setData(List<T> data) {
        this.Data = data;
    }
    public void AddData(T data) {
        this.Data.add(data);
    }
    public void RemoveData(T data) {
        this.Data.remove(data);
    }
    public int size() {
        return Data.size();
    }
}
