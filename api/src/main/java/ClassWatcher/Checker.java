package ClassWatcher;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Checker {
    private String notificationID;
    private String courseCat;
    private String courseID;
    private int sectionNum;
    private String term;
    private NotificationMethod notificationMethod;
}
