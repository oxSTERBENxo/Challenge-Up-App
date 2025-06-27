using ChallengeUpFrontend.Models;
namespace ChallengeUpFrontend.Services

{
    public class MockChallengeService
    {
        public Task<List<Challenge>> GetDailyChallengesAsync()
        {
            var list = new List<Challenge>
            {
                new Challenge { Id = 1, Text = "Do 10 push-ups", Category = "Fitness", Points = 10 },
                new Challenge { Id = 2, Text = "Drink 2 glasses of water", Category = "Health", Points = 5 },
                new Challenge { Id = 3, Text = "Write 3 goals for today", Category = "Productivity", Points = 7 }
            };

            return Task.FromResult(list);
        }
    }
}