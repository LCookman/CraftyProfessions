package magnarisa.craftyprofessions.commands.ProfessionCommands;

import magnarisa.craftyprofessions.commands.ProfessionCommand;
import magnarisa.craftyprofessions.container.CommandData;
import magnarisa.craftyprofessions.container.CraftyPlayer;

/**
 * This command will handle the Profile command execution.
 */
public class CommandProfile extends ProfessionCommand
{
    public CommandProfile ()
    {
        super (new CommandData (
            "profile",
            "This command will give you a book that will display your profile in depth",
            "/prof profile",
            "craftyprofessions.use.profile"));
    }

    @Override
    public boolean execute (CraftyPlayer sender, String... args)
    {
        sender.sendMessage ("You have just executed /prof profile");

        return true;
    }
}