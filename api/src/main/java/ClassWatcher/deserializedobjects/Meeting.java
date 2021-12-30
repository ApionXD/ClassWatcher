package ClassWatcher.deserializedobjects;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Meeting {
    private String days;
    private int startTime;
    private int endTime;

}
