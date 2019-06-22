package me.ghui.v2er.module.settings;

import butterknife.BindView;
import me.ghui.toolbox.android.Assets;
import me.ghui.v2er.R;
import me.ghui.v2er.general.ColorModeReloader;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.topic.HtmlView;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseToolBar;

public class UserManualActivity extends BaseActivity implements HtmlView.OnHtmlRenderListener {
    private final String KEY_SCROLL_Y = KEY("key_scroll_y");
    @BindView(R.id.htmlview)
    HtmlView mHtmlView;

    @Override
    protected int attachLayoutRes() {
        return R.layout.user_manual;
    }

    @Override
    protected void configToolBar(BaseToolBar toolBar) {
        super.configToolBar(toolBar);
        Utils.setPaddingForStatusBar(toolBar);
        Utils.setPaddingForNavbar(mHtmlView);
    }

    @Override
    protected void init() {
        super.init();
        mHtmlView.setOnHtmlRenderListener(this);
        String localContent = Assets.getString("html/user_manual.html", this);
        mHtmlView.loadContentView(localContent);
    }

    @Override
    protected void reloadMode(int mode) {
        ColorModeReloader.target(this)
                .putExtra(KEY_SCROLL_Y, mHtmlView.getScrollY())
                .reload();
    }

    @Override
    public void onRenderCompleted() {
        int scrollY = getIntent().getIntExtra(KEY_SCROLL_Y, -1);
        if (scrollY != -1) {
            mHtmlView.scrollTo(0, scrollY);
        }

    }
}
