public class Todo extends Task{
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toDataString() {
        return super.toDataString("T");
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
