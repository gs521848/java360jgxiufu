.class public Lcom/wlb3733/SplashActivity;
.super Landroid/app/Activity;
.source "SplashActivity.java"


# direct methods
.method public constructor <init>()V
    .locals 0

    .prologue
    .line 19
    invoke-direct {p0}, Landroid/app/Activity;-><init>()V

    return-void
.end method


# virtual methods
.method protected onCreate(Landroid/os/Bundle;)V
    .locals 6
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;
        .annotation build Landroid/support/annotation/Nullable;
        .end annotation
    .end param

    .prologue
    const/4 v2, -0x1

    .line 23
    invoke-super {p0, p1}, Landroid/app/Activity;->onCreate(Landroid/os/Bundle;)V

    .line 24
    new-instance v0, Landroid/widget/RelativeLayout;

    invoke-direct {v0, p0}, Landroid/widget/RelativeLayout;-><init>(Landroid/content/Context;)V

    .line 25
    .local v0, "relativeLayout":Landroid/widget/RelativeLayout;
    new-instance v1, Landroid/widget/FrameLayout$LayoutParams;

    invoke-direct {v1, v2, v2}, Landroid/widget/FrameLayout$LayoutParams;-><init>(II)V

    invoke-virtual {v0, v1}, Landroid/widget/RelativeLayout;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 26
    const v1, 0x7f020283

    invoke-virtual {v0, v1}, Landroid/widget/RelativeLayout;->setBackgroundResource(I)V

    .line 27
    invoke-virtual {p0, v0}, Lcom/wlb3733/SplashActivity;->setContentView(Landroid/view/View;)V

    .line 28
    new-instance v1, Lcom/wlb3733/SplashActivity$1;

    invoke-direct {v1, p0}, Lcom/wlb3733/SplashActivity$1;-><init>(Lcom/wlb3733/SplashActivity;)V

    const/4 v2, 0x0

    const-wide/16 v4, 0xbb8

    .line 36
    invoke-virtual {v1, v2, v4, v5}, Lcom/wlb3733/SplashActivity$1;->sendEmptyMessageDelayed(IJ)Z

    .line 37
    return-void
.end method
