namespace ChallengeUpFrontend.Models
{
    public class Challenge
    {
        public string ToDo { get; set; }
        public string completedAt { get; set; }
        public int PointsAmount { get; set; }
        public string Category { get; set; }
        public bool isCompleted { get; set; }
    }
}