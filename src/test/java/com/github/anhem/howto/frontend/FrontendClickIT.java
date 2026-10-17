package com.github.anhem.howto.frontend;

import com.github.anhem.howto.client.urlhaus.UrlHausClient;
import com.github.anhem.howto.testutil.TestApplication;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class FrontendClickIT extends TestApplication {

    @LocalServerPort
    private int port;

    @MockitoBean
    private UrlHausClient urlHausClient;

    @Test
    void userCanAuthenticateBrowseForumCreatePostAndReply() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            String baseUrl = "http://localhost:" + port;
            page.navigate(baseUrl);

            assertThat(page.locator("#auth-status")).hasText("Not logged in");

            page.locator("#navbar-login-btn").click();
            page.locator("#login-username-input").fill("admin");
            page.locator("#login-password-input").fill("superSecret1!");
            page.locator("#login-submit-btn").click();

            assertThat(page.locator("#auth-status")).hasText("admin");
            assertThat(page.locator("#user-role-badge")).hasText("ADMINISTRATOR");

            page.locator("#navbar-accounts-btn").click();
            assertThat(page.locator("h2")).containsText("User Accounts");
            page.locator("#breadcrumb-forum-home").click();

            page.locator("#create-category-btn").click();
            String categoryName = "Playwright Discussions " + System.currentTimeMillis();
            page.locator("#category-name-input").fill(categoryName);
            page.locator("#category-desc-input").fill("Category created during automated click test");
            page.locator("#category-submit-btn").click();

            Locator categoryCard = page.locator(".card", new Page.LocatorOptions().setHasText(categoryName));
            assertThat(categoryCard).isVisible();
            categoryCard.click();

            assertThat(page.locator("#category-page-title")).hasText(categoryName);
            page.locator("#create-post-btn").click();
            String postTitle = "E2E Test Post " + System.currentTimeMillis();
            page.locator("#post-title-input").fill(postTitle);
            page.locator("#post-body-input").fill("This is the post content created by Playwright click test.");
            page.locator("#post-submit-btn").click();

            Locator postCard = page.locator(".card", new Page.LocatorOptions().setHasText(postTitle));
            assertThat(postCard).isVisible();
            postCard.click();

            assertThat(page.locator("#post-detail-title")).hasText(postTitle);
            String replyContent = "Automated reply test from Playwright.";
            page.locator("#reply-content-input").fill(replyContent);
            page.locator("#reply-submit-btn").click();

            assertThat(page.locator("#replies-container")).containsText(replyContent);

            page.locator("#navbar-logout-btn").click();
            assertThat(page.locator("#auth-status")).hasText("Not logged in");

            String newUsername = "user" + System.currentTimeMillis();
            page.locator("#navbar-login-btn").click();
            page.locator("#tab-signup-btn").click();
            page.locator("#signup-username-input").fill(newUsername);
            page.locator("#signup-email-input").fill(newUsername + "@example.com");
            page.locator("#signup-firstname-input").fill("New");
            page.locator("#signup-lastname-input").fill("Member");
            page.locator("#signup-password-input").fill("superSecretPassword123!");
            page.locator("#signup-submit-btn").click();

            assertThat(page.locator("#auth-status")).hasText(newUsername);
            assertThat(page.locator("#user-role-badge")).hasText("USER");

            page.locator("#navbar-logout-btn").click();
            assertThat(page.locator("#auth-status")).hasText("Not logged in");
        }
    }
}
