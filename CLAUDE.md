# Remainder — AI Development Instructions

Remainder is an offline-first Android reminder app. Kotlin + Jetpack Compose +
Material 3, Clean Architecture + MVVM, Room, Hilt, Navigation Compose,
AlarmManager-based local reminders. No backend, no cloud, no networking —
this is a hard constraint, not a placeholder.

The full product/technical specification lives in the task history that
created this repository (product vision, screens, data model, reminder
architecture, design system values, phase plan). Treat this file as the
condensed operating rules for any agent working in this repo; consult the
spec for feature-level detail when implementing a given phase.

## Non-negotiable rules

1. **Phase by phase.** Do not implement multiple phases in one pass. Finish
   and report on the current phase, then stop and wait for approval before
   starting the next one. See "Phase order" below.
2. **Preserve architecture.** UI → ViewModel → UseCase → Repository → DAO.
   Business logic never lives in a `@Composable`. Don't introduce a
   different architecture without explaining what/why/affected files/
   alternatives first.
3. **No cloud, ever, unless explicitly requested.** No backend, no
   Firebase/Supabase, no auth, no remote APIs, no sync, no analytics, no ads.
   Every dependency must have a clear reason — don't add one just because
   it's common in Android apps.
4. **Reuse components.** Check `ui/components` and `ui/theme` before adding
   a new Compose component or hardcoding a color/spacing/shape value.
5. **Small, focused commits.** Don't rewrite unrelated files. Clear messages
   (`feat: ...`, `fix: ...`, `chore: ...`) — never "changes"/"update".
6. **Build after meaningful changes**, fix compile errors, and report
   failures plainly — never silently swallow them.

## Phase order (do not skip ahead)

0. Foundation — Gradle/Kotlin/Compose/Material 3 project, builds successfully
1. Design system — theme, colors, typography, spacing, shapes, base components
2. Navigation + core UI shells (Home, Calendar, Completed, Settings, Add Action)
3. Room database — entities, DAOs, repositories, use cases
4. Action management — create/read/update/delete/complete/restore
5. Reminder engine — AlarmManager, BroadcastReceiver, notifications, snooze,
   reboot rescheduling, permission handling
6. Recurrence — daily/weekly/monthly/yearly (custom only if it stays clean)
7. Calendar — month navigation, date selection, date-scoped actions
8. Search + filters
9. Settings — theme, notification/reminder preferences
10. Quality pass — edge cases, accessibility, empty/error states, polish
11. Testing — unit, DB, reminder, Compose UI tests
12. Release — versioning, signing config, AAB/APK

## Known environment constraint (this sandbox only)

`dl.google.com` — and `maven.google.com`, which 301-redirects every artifact
request to `dl.google.com` — is blocked by this session's egress policy.
Maven Central and the Gradle Plugin Portal's own artifacts are reachable, but
AndroidX, Compose, Material 3, Room, Hilt, and the Android Gradle Plugin are
all published exclusively through Google's Maven repo, so **this sandbox
cannot resolve dependencies or fully compile the app**. The project is
configured normally (`google()` in `settings.gradle.kts`, standard
`plugins {}` + version catalog) because that is correct for Android Studio
and CI, which have normal internet access. Full builds must be verified in
Android Studio locally or in a CI pipeline with unrestricted network access
(e.g. GitHub Actions). Do not "fix" this by disabling TLS verification,
routing around the proxy, or swapping in unofficial mirrors.

## Data model reminders

- Store dates/times with modern `java.time` types via Room `TypeConverter`s,
  not as raw strings.
- Reminder scheduling is a side effect of action create/edit/delete/complete
  — keep it behind a dedicated `AlarmScheduler`, called from use cases, never
  from Composables or directly from DAOs.
- Recurrence is a typed model/enum, not a free-form cron-like string.
