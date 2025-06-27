using Microsoft.AspNetCore.Components.Web;
using Microsoft.AspNetCore.Components.WebAssembly.Hosting;
using ChallengeUpFrontend;
using ChallengeUpFrontend.Services;


var builder = WebAssemblyHostBuilder.CreateDefault(args);
builder.RootComponents.Add<App>("#app");
builder.RootComponents.Add<HeadOutlet>("head::after");
builder.Services.AddScoped(sp => new HttpClient { BaseAddress = new Uri(builder.HostEnvironment.BaseAddress) });

builder.Services.AddScoped<MockChallengeService>();
builder.Services.AddScoped<MockUserStatsService>();
builder.Services.AddSingleton<MockChallengeService>();
builder.Services.AddSingleton<MockUserStatsService>();



await builder.Build().RunAsync();