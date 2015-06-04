/**
 * Created by Nick.
 */
public class todo {
/*


    Fix textviews running length issue by using ellipsize which adds "..." automatically at the end of lines that
    are cut off.
    http://stackoverflow.com/questions/9149846/can-i-limit-textviews-number-of-characters


Refactor navdrawer activity: set currentOption = whatever option in the actual case statements rather
than having them in individual activities.

    Settings Menu:
    PIN         on/off pill
        Suboption: apply to all
        Suboption: apply only to accounts activity

    Demo
        Subtitle: Do not use. For development purposes only.  Will be removed in official release.



    Pseudo code for universal PIN:

    Create a super class, base activity, which extends Activity
    NavDrawerActivity will extend to base activity.

    declare and initialize a private static final int NOT_SET = 73059;

    declare and initialize a private static int UNLOCK_CODE = NOT_SET;

    override onCreate(), onResume() and check if UNLOCK_CODE == NOT_SET;

    if (UNLOCK_CODE == NOT_SET)
        proceed normally

    else if (UNLOCK_CODE != NOT_SET)
        start PinActivity and ask user to input USER_PIN. pressing okay will result in checking
        if pin is correct.

        PinActivity has backbutton set to Home.

            if (USER_PIN == UNLOCK_CODE)
                proceed with original activity

                else display incorrect pin toast message



*/
}
