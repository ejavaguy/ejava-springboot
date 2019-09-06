package info.ejava_student.assignment1.beanfactory.race.dto;

public class RaceDTO {
    private String name;

    public RaceDTO(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "{" + name + "}";
    }
}
