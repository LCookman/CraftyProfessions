package magnarisa.craftyprofessions.exceptions;

/**
 * This exception is thrown when a certain config file cannot be found.
 */
public class ConfigNotFoundException extends Exception
{
    public ConfigNotFoundException (String message)
    {
        super (message);
    }
}
