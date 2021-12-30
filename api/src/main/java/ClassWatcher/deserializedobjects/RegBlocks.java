package ClassWatcher.deserializedobjects;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class RegBlocks {
    private Section[] sections;

    public Section[] getSections() {
        return sections;
    }
}
