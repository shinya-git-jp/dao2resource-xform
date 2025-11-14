/** @type {import('@storybook/test-runner').TestRunnerConfig} */
module.exports = {
  // Optional: add console logs for debug
  setup() {
    console.log('Running tests in Japanese locale...');
  },
async preRender(page) {
    page.setDefaultTimeout(30000); // 30 seconds
  },

  // Optional: test timeout per story
  testTimeout: 30000, // 30 seconds
  // Set Japanese locale
  browserContextOptions: {
    locale: 'ja-JP',
  },

  // Optional: Set additional launch options
  launchOptions: {
    headless: true,
    args: ['--lang=ja-JP'], // ensures language for Chromium
  },
};