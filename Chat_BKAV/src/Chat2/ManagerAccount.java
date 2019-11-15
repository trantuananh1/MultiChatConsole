package Chat2;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ManagerAccount {

     public List<Account> accs = new ArrayList<>();
     public void writeFile(String file) {
        try {
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(f);
            oos.writeObject(accs);
            oos.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void readFile(String file) {
        try {
            FileInputStream f = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(f);
            accs = (List<Account>) ois.readObject();
            ois.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public boolean checkLogin(String username, String password) {
        for (Account a : accs) {
            if (a.getUsername().equalsIgnoreCase(username) && a.getPassword().equalsIgnoreCase(password)) {
                return true;
            }
        }
        return false;
    }
}