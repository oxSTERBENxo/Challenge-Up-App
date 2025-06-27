using ChallengeUpFrontend.Models;
namespace ChallengeUpFrontend.Services

{
    public class MockUserStatsService
    {
        public Task<UserStats> GetUserStatsAsync()
        {
            return Task.FromResult(new UserStats
            {
                Score = 125,
                Gems = 10,
                Streak = 4
            });
        }
    }
}