package rgou.model.element;

public class Hello {
    protected static final String protectedString = "im a protected string";
    public void sayHi() {
        System.out.println("hi - method");
    }

    void sayHiNoModifier() {
        System.out.println("hi, im protected - method with no modifier");
    }
}
