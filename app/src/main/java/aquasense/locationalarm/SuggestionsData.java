package aquasense.locationalarm;
public class SuggestionsData  {

    String Type;
    String Suggestion;

    public SuggestionsData() {
    }

    public SuggestionsData(String type, String suggestion) {
        Type = type;
        Suggestion = suggestion;
    }

    public String getType() {

        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getSuggestion() {
        return Suggestion;
    }

    public void setSuggestion(String suggestion) {
        Suggestion = suggestion;
    }
}
