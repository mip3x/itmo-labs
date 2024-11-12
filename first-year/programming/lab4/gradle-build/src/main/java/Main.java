import story.Story;
import story.StoryIsNotReadyException;

public class Main {
    public static void main(String[] args) {
        Story story = new Story();
        
        try {
            story.startTheTell();
            System.out.println(Story.PersonCounter.getPersonCount());
        }
        catch (StoryIsNotReadyException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
