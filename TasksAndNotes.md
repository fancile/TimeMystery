# Tasks and Notes List

**How to use this doc:** tick a box (`[ ]` → `[x]`) when a task is done, and move a short line describing it into the **Completed Tasks Log** at the top, with your name and the date. That way everyone can see progress at a glance without reading the whole doc.

---

## ✅ Completed Tasks Log

Add a line here every time you finish something + your name

- [x] Decide the reason Dan's band is cut - Maya
- [x] Epoch 1 room scene - Maya

---

## 🚩 URGENT — Needs Team Discussion Before Building Further

### Verdict logic currently conflicts with the brief

Current notes say: _"the player will win the game so long as they have the correct rationale behind their choice. It does not matter whether they click the correct one or not."_

This does **not** match the brief, which requires two **independent** judgments combined into **four distinct verdicts**:

1. Correct intervention + correct, supported, coherent rationale
2. Correct intervention + incorrect/unsupported/incoherent rationale
3. Incorrect intervention + correct, supported, coherent rationale
4. Incorrect intervention + incorrect/unsupported/incoherent rationale

- [ ] Team decision: confirm we are building all four verdicts, not a rationale-only win condition.
- [ ] Java code must independently determine whether the **chosen intervention** is correct (deterministic, no LLM involved).
- [ ] A **separate LLM call** evaluates only the **rationale** (diagnosis correct? supported by evidence actually discovered this run? coherent explanation of intended effect?).
- [ ] Combine both results into one of the four verdicts above (simple lookup/if-else, not another LLM call).

---

## Scenario Overview

- **Recurring NPC:** Alex, the hall owner.
- **Culprit:** Dan, the drummer — burns down the hall out of anger after his band is cut from the lineup.
- **Cause of the fire:** Dan, in his anger, breaks an electrical panel with his drumstick (Epoch 2), and burns the hall down that night by setting the electrical panel on fire with matchsticks.
- These are the only NPCs/characters in the game.

### Epoch 1 — Finalising the Schedule

Alex is putting together the final concert lineup.

- [x] **Decide the reason Dan's band is cut** (pick one, or confirm already decided):
  - Option A: Funding pressure — the concert can't afford that many acts.
  - Option B: Dan's band had a bad audition; Alex initially keeps them out of sympathy, then cuts them.
  - Option C: Hall management wants a shorter show.
- [x] **Decide Epoch 1's core interactable**, matching whichever option is chosen above:
  - Option A → draft schedule sheet, one act crossed out, note in Dan's handwriting ("not fair — talk to me before you decide, D.")
  - Option B → audition/performance notes suggesting underperformance
  - Option C → funding notice from hall management

### Epoch 2 — Final Rehearsal

- Two nights before the concert.
- The cut is now final. Dan is furious.
- Dan damages the electrical panel with his drumstick.
- **Core interactable:** the electrical panel — opens to reveal tampering/damage.

### Epoch 3 — Aftermath

- Alex stands outside the burnt hall, 20 years later.
- Alex knows there was a fire.
- [ ] **Decide:** does Alex know/believe the cause was the electrical panel (accidental theory), or does he have no theory at all? This affects his prompt directly.
- **Core interactable:** matchsticks near the wiring, confirming deliberate ignition.

### On Dan's Presence

Dan is **not** a chattable NPC — Alex is the only recurring NPC.

- [ ] **Open question — confirm with tutor:** can Dan still be clicked for a description label (as in Alpha), or should this be removed/changed to a hover-only reveal, given the brief's _"non-interactive people... must not function as characters"_ rule? Current lean: hover-only, no click, to be safe.

---

## Outstanding Decisions

- [x] Epoch 1 core interactable (see above).
- [ ] Epoch 3 — Alex's belief about the fire's cause (see above).
- [ ] The four interventions — design all four (one correct per Java logic, three plausible-but-wrong), each applied in Epoch 1 or 2, each with a predetermined Epoch 3 consequence. Options must not explicitly reveal the diagnosis.
- [ ] Timer lengths — confirm: untimed briefing → 5 min investigation → 30s intervention selection → 60s rationale → untimed verdict.
- [ ] CSS / visual styling — how much time to spend on this, given functionality is prioritised over graphics.
- [ ] Dan click-vs-hover decision (see above).

---

## Task List

### Design / Content

- [x] Finalise Epoch 1 interactable and its exact discoverable text/state.
- [ ] Write three epoch-specific prompts for Alex (Epoch 1, 2, 3), each with correct knowledge/memory boundaries.
- [ ] Design the four interventions and their pre-authored Epoch 3 consequences.
- [ ] Write the rationale-evaluator LLM prompt (likely high reasoning effort). Must check: correct diagnosis, evidence actually discovered this run, coherent explanation of intended effect.

### Build — New Scenes

- [ ] Untimed briefing scene (situation, objective, travel controls, how interactables are indicated — must NOT reveal the four interventions).
- [ ] Dedicated epoch-selection scene, separate from the three epoch rooms.
- [x] Epoch 1 room scene (NPC, room, core interactable, integrated chat, all visible together).
- [x] Epoch 2 room scene (same requirements).
- [x] Epoch 3 room scene (same requirements).
- [ ] Intervention-selection scene (reveals four interventions after investigation ends).
- [ ] Rationale-writing scene (free-text input, 60s timer).
- [ ] Verdict scene (shows one of four outcomes, untimed, stays visible until player chooses to continue).
- [ ] Incomplete-round scene (shown if investigation timer expires without all 3 conversations AND all 3 interactables complete) — must have a replay option, must NOT lead to an intervention.

### Build — Logic

- [x] Track "has spoken to NPC" and "has interacted with interactable" per epoch (Java-owned state).
- [ ] Gate early intervention: block it unless ALL three conversations AND ALL three interactables are complete.
- [ ] Extend memory-passing so each later epoch's NPC receives relevant summarised state from earlier epochs — summaries only, never raw transcript dumps.
- [x] Investigation timer: 5 minutes, visible in every epoch including during chat/LLM waits, continues counting even while an LLM request is pending.
- [ ] At investigation end (timeout or early finish): block travel, conversation, interactables, evidence collection immediately.
- [ ] Ignore any LLM reply that arrives after its phase has already ended — must not alter evidence or state.
- [ ] Intervention-selection timer (30s), early submission allowed, auto-submit existing selection at expiry, "no selection" → incomplete-round-style handling (no intervention outcome produced).
- [ ] Rationale-writing timer (60s), early submission allowed, auto-submit non-blank text at expiry.
- [ ] Blank rationale at expiry → skip the evaluator call entirely, use a standard "unsupported rationale" result.
- [ ] Second, separate LLM call for rationale evaluation (not reused from Alex's conversational calls).
- [ ] Java-side deterministic logic for "was the chosen intervention correct" (see URGENT section above).
- [ ] Combine intervention correctness + rationale evaluation into the four required verdict states (see URGENT section above).
- [ ] Replay resets: timeline, conversations, evidence records, interactables, timers, stage, selected intervention, rationale, evaluation, verdict. (Alpha's reset logic is a good base — re-check it covers all of the above once extended to 3 epochs.)

### Build — Error Handling (currently missing from plan entirely)

- [ ] A failed/malformed Alex response must not freeze or crash the app, and must not discover evidence or alter timeline state.
- [ ] While a stage is still open, a failed LLM request must show a visible, recoverable error and let the player retry.
- [ ] If rationale evaluation fails: preserve the player's written rationale, still show the Java-determined intervention consequence, clearly mark the evaluation as unavailable, and allow a retry.

### Polish

- [ ] Enter key sends chat message (plus keep the visible Send button).
- [ ] Chat auto-scrolls to latest message.
- [ ] Window size stays constant across every scene change (explicit client feedback from Alpha — treat as high priority, not just "nice to have").
- [ ] Visible pending/loading indicator whenever any LLM request is in progress (not just chat — also the rationale evaluator).
- [ ] Mute control for sound effects/music, since we now have sound effects in Epoch 1 (drawer open/close) — must be able to mute, per brief.
- [ ] Optional: pre-made, short TTS (not generated on the fly).
- [ ] Optional: background music, off/mutable by default.

### Testing

- [ ] Test on a clean clone on a different machine (resource paths, window sizing, API config).
- [ ] Full playthrough covering all four verdict combinations specifically (not just "a" playthrough — deliberately trigger each of the 4 outcomes at least once).
- [ ] Confirm nothing freezes the JavaFX thread at any point, including rationale evaluation.
- [ ] Deliberately test a failed/slow LLM response (e.g. disconnect network briefly) to confirm the retry/error-handling requirements actually work, not just the happy path.
- [ ] Test the incomplete-round path: let the timer run out without visiting all NPCs/interactables, confirm it shows the incomplete screen and does NOT offer an intervention.

---

## Notes

- Scene size is `prefHeight="420.0" prefWidth="790.0"` — keep this consistent across every scene for the "window size doesn't change" requirement.
