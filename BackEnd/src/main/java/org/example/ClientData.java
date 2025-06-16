package org.example;

public class ClientData {
    private String Username;
    private String Email;
    private String Password;

    public ClientData(String name, String email, String password) {
        this.Username = name;
        this.Email = email;
        this.Password = password;
    }

    public String getUsername() {
        return Username;
    }
    public String getEmail() {
        return Email;
    }
    public String getPassword() {
        return Password;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof ClientData)) return false;
        ClientData that = (ClientData) o;
        return  (Username.equals(that.Username) && Password.equals(that.Password) && Email.equals(that.Email));
    }
}
