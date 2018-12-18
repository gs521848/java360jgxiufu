.class Lcom/wlb3733/SplashActivity$1;
.super Landroid/os/Handler;
.source "SplashActivity.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/wlb3733/SplashActivity;->onCreate(Landroid/os/Bundle;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/wlb3733/SplashActivity;


# direct methods
.method constructor <init>(Lcom/wlb3733/SplashActivity;)V
    .locals 0
    .param p1, "this$0"    # Lcom/wlb3733/SplashActivity;

    .prologue
    .line 29
    iput-object p1, p0, Lcom/wlb3733/SplashActivity$1;->this$0:Lcom/wlb3733/SplashActivity;

    invoke-direct {p0}, Landroid/os/Handler;-><init>()V

    return-void
.end method


# virtual methods
.method public handleMessage(Landroid/os/Message;)V
    .locals 3
    .param p1, "msg"    # Landroid/os/Message;

    .prologue
    .line 32
    iget-object v0, p0, Lcom/wlb3733/SplashActivity$1;->this$0:Lcom/wlb3733/SplashActivity;

    iget-object v1, p0, Lcom/wlb3733/SplashActivity$1;->this$0:Lcom/wlb3733/SplashActivity;

    invoke-virtual {v1}, Lcom/wlb3733/SplashActivity;->getPackageName()Ljava/lang/String;

    move-result-object v1

    const-string v2, "com.zhizhuang.MainActivity"

    invoke-static {v0, v1, v2}, Lcom/wlb3733/xhd3733Activity;->StartAnotherActivity(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)V

    .line 33
    iget-object v0, p0, Lcom/wlb3733/SplashActivity$1;->this$0:Lcom/wlb3733/SplashActivity;

    invoke-virtual {v0}, Lcom/wlb3733/SplashActivity;->finish()V

    .line 34
    return-void
.end method
