export function getAuthorizationHeaderContent(user, password)
{
    const token = user + ":" + password;

    // Should I be encoding this value????? does it matter???
    // Base64 Encoding -> btoa
    const hash = btoa(token);

    return "Basic " + hash;
}