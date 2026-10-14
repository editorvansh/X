const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const ADMIN_UID = "admin_789";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- SECURITY BOUNDS TESTS ---

test("Unauthenticated user: cannot read users or tournaments", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").get());
  await assertFails(unauthDb.collection("tournaments").get());
});

test("Authenticated user: cannot read another user profile directly", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      email: "bob@example.com",
      displayName: "Bob Pro",
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
});

test("Authenticated user: can read own profile and create own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      email: "alice@example.com",
      displayName: "Alice FF",
      inGameId: "12345678",
      inGameName: "AliceGamer",
      totalMatches: 0,
      totalKills: 0,
      totalWins: 0,
      totalPoints: 0,
      createdAt: new Date(),
    })
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: cannot impersonate someone else on create", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      email: "bob@example.com",
      displayName: "Bob",
      createdAt: new Date(),
    })
  );
});

test("Tournaments: signed in user can view tournaments", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("tournaments").doc("tourney_1").set({
      id: "tourney_1",
      title: "Championship 2026",
      format: "Solo",
      map: "Bermuda",
      maxSlots: 48,
      filledSlots: 10,
      status: "upcoming",
      roomReleased: false,
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("tournaments").doc("tourney_1").get());
});

test("Admin: can create tournament", async () => {
  const adminDb = testEnv.authenticatedContext(ADMIN_UID, {
    email: "saritakumari9818422028@gmail.com",
    email_verified: true,
  }).firestore();

  await assertSucceeds(
    adminDb.collection("tournaments").doc("tourney_2").set({
      id: "tourney_2",
      title: "Pro Squad Clash",
      format: "Squad",
      map: "Purgatory",
      maxSlots: 12,
      filledSlots: 0,
      status: "upcoming",
      roomReleased: false,
      createdAt: new Date(),
    })
  );
});

test("Non-admin: cannot create tournament", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("tournaments").doc("tourney_hacked").set({
      id: "tourney_hacked",
      title: "Hacked Tourney",
      format: "Solo",
      map: "Bermuda",
      maxSlots: 48,
      filledSlots: 0,
      status: "upcoming",
      roomReleased: false,
      createdAt: new Date(),
    })
  );
});

test("Registration: user can register for tournament", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("tournaments").doc("t_1").set({
      id: "t_1",
      title: "Solo Cup",
      format: "Solo",
      map: "Bermuda",
      maxSlots: 48,
      filledSlots: 0,
      status: "upcoming",
      roomReleased: false,
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("registrations").doc("t_1_" + ALICE_UID).set({
      id: "t_1_" + ALICE_UID,
      tournamentId: "t_1",
      userId: ALICE_UID,
      playerDisplayName: "Alice",
      inGameId: "FF998877",
      inGameName: "AliceKillz",
      status: "confirmed",
      createdAt: new Date(),
    })
  );
});

test("Registration: cannot register another user", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("tournaments").doc("t_1").set({
      id: "t_1",
      title: "Solo Cup",
      format: "Solo",
      map: "Bermuda",
      maxSlots: 48,
      filledSlots: 0,
      status: "upcoming",
      roomReleased: false,
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("registrations").doc("t_1_" + BOB_UID).set({
      id: "t_1_" + BOB_UID,
      tournamentId: "t_1",
      userId: BOB_UID,
      inGameId: "BOB123",
      inGameName: "Bob",
      status: "confirmed",
      createdAt: new Date(),
    })
  );
});
