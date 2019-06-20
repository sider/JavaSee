public class BadBooleanEqual1 {
    {
        var x = "foo";
        System.out.println(("foo".equals(x)) == true);
        System.out.println(true == ("foo".equals(x)));
    }
}