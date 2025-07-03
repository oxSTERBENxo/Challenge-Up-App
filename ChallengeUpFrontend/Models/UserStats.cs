using System.Collections.Generic;
namespace ChallengeUpFrontend.Models
{
    public class UserStats
    {
        public string Username { get; set; }
        public int Points { get; set; }
        public int Days { get; set; }
        public int Gems { get; set; }
        public int Streak { get; set; }
        public bool Completed { get; set; }
        
        public List<Challenge> CompletedTasks { get; set; } = new List<Challenge>();
        public Queue<bool> Last7Days { get; set; } = new Queue<bool>();
        public Queue<bool> Last7DaysTemp { get; set; } = new Queue<bool>();
        public List<Challenge> CompletedTasksTemp { get; set; } = new List<Challenge>();
        
        public int PointsTemp { get; set; }
        public int DaysTemp { get; set; }
        
        public UserStats()
        {
            CompletedTasks = new List<Challenge>();
            Last7Days = new Queue<bool>();
            Last7DaysTemp = new Queue<bool>();
            CompletedTasksTemp = new List<Challenge>();
        }
    }
}