import story.Story;
import story.StoryIsNotReadyException;

public class Main {
    public static void main(String[] args) {
        Story story = new Story();
        
        try {
            story.startTheTell();
        }
        catch (StoryIsNotReadyException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
