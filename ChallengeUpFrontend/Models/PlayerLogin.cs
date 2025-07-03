namespace ChallengeUpFrontend.Models
{
    public class PlayerLogin
    {
        public string UserName { get; set; }
        public string Password { get; set; }

        public PlayerLogin(string UserName, string Password)
        {
            this.UserName = UserName;
            this.Password = Password;
        }

    }
}