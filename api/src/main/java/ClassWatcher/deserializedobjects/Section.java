package ClassWatcher.deserializedobjects;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Section {
    private int openSeats;
    private Instructor[] instructors;
    private Meeting[] meetings;

}
