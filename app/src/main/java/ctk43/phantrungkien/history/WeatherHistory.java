package ctk43.phantrungkien.history;

public class WeatherHistory {
    public String txtID,txtDate, txtMaxTemp, txtMinTemp;

    public WeatherHistory(String txtId,String txtDate, String txtMaxTemp, String txtMinTemp) {
        this.txtID=txtId;
        this.txtDate = txtDate;
        this.txtMaxTemp = txtMaxTemp;
        this.txtMinTemp = txtMinTemp;
    }

    public String getTxtID() {
        return txtID;
    }

    public void setTxtID(String txtID) {
        this.txtID = txtID;
    }

    public String getTxtDate() {
        return txtDate;
    }

    public void setTxtDate(String txtDate) {
        this.txtDate = txtDate;
    }

    public String getTxtMaxTemp() {
        return txtMaxTemp;
    }

    public void setTxtMaxTemp(String txtMaxTemp) {
        this.txtMaxTemp = txtMaxTemp;
    }

    public String getTxtMinTemp() {
        return txtMinTemp;
    }

    public void setTxtMinTemp(String txtMinTemp) {
        this.txtMinTemp = txtMinTemp;
    }
}
