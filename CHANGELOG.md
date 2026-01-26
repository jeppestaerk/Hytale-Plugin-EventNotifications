# Changelog

All notable changes to this project will be documented in this file.

## [1.0.0] - 2026-01-26

### Added
- Initial release
- **ntfy** - Push notifications to phone, desktop, or web
- **Discord** - Webhook notifications with rich embeds
- **Slack** - Webhook notifications with attachments
- **Webhooks** - Generic HTTP webhooks for custom integrations

### Supported Events
- Server start/stop
- Player join/leave
- Player chat (disabled by default)
- Game mode changes
- Permission changes (group and player)

### Features
- Per-target event configuration
- Customizable message templates with placeholders
- Markdown support (ntfy, Discord, Slack)
- Authentication support (Basic, Bearer, custom headers)
- Async HTTP with virtual threads
