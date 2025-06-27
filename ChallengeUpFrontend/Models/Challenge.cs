namespace ChallengeUpFrontend.Models
{
    public class Challenge
    {
        public int Id { get; set; }
        public string Text { get; set; }
        public string Category { get; set; }
        public int Points { get; set; }
        public bool IsCompleted { get; set; }
    }
}