const { getDefaultConfig } = require('expo/metro-config');

const config = getDefaultConfig(__dirname);

// Prevent the "Sign in to your account" prompt
process.env.EXPO_NO_DOCTOR = '1';

module.exports = config;
