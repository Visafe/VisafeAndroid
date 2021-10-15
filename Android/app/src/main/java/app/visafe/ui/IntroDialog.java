
package app.visafe.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import app.visafe.R;
import app.visafe.sys.PersistentState;
import app.visafe.sys.VpnController;

// Fragment representing the full-screen first-run experience.  This intro is launched by the main
// activity.
public class IntroDialog extends DialogFragment {
  private static final int NUM_PAGES = 1;

  // Avoid duplicate instances of this dialog.  This can occur when rotating the app, which would
  // otherwise cause the main activity to launch an additional instance of this dialog.
  private static boolean isShown = false;
  static boolean shouldShow(Activity activity) {
    return !isShown && !PersistentState.getWelcomeApproved(activity);
  }

  @Override
  public int getTheme() {
    return R.style.FullScreenDialog;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
      savedInstanceState) {
    View welcomeView = inflater.inflate(R.layout.intro_pager, container);

    Adapter adapter = new Adapter(this);
    ViewPager2 pager = welcomeView.findViewById(R.id.welcome_pager);
    pager.setAdapter(adapter);

    Drawable leftChevron = getResources().getDrawable(R.drawable.ic_chevron_left);
    Drawable rightChevron = getResources().getDrawable(R.drawable.ic_chevron_right);

    final Button backButton = welcomeView.findViewById(R.id.intro_back);
    final Button nextButton = welcomeView.findViewById(R.id.intro_next);

    final Button leftButton, rightButton;
    if (Rtl.isRtl(this)) {
      leftButton = nextButton;
      rightButton = backButton;
    } else {
      leftButton = backButton;
      rightButton = nextButton;
    }
    leftButton.setCompoundDrawablesWithIntrinsicBounds(leftChevron, null, null, null);
    rightButton.setCompoundDrawablesWithIntrinsicBounds(null, null, rightChevron, null);

    backButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        int currentItem = pager.getCurrentItem();
        if (currentItem > 0) {
          pager.setCurrentItem(currentItem - 1);
        }
      }
    });

    nextButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        int currentItem = pager.getCurrentItem();
        if (currentItem < NUM_PAGES - 1) {
          pager.setCurrentItem(currentItem + 1);
        }
      }
    });

    Button acceptButton = welcomeView.findViewById(R.id.intro_accept);
    acceptButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        PersistentState.setWelcomeApproved(getContext(), true);
        VpnController.getInstance().start(getContext());
        MainActivity.login = 0;
        dismiss();
      }
    });

    pager.registerOnPageChangeCallback(
            new ButtonVisibilityUpdater(backButton, nextButton, acceptButton));

    // Register the dots for actions and updates.
    TabLayout dots = welcomeView.findViewById(R.id.intro_dots);
    new TabLayoutMediator(dots, pager, true, (tab, position) -> {}).attach();

    final Button intro_button = welcomeView.findViewById(R.id.button_intro);
    intro_button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        PersistentState.setWelcomeApproved(getContext(), true);

        MainActivity.login = 0;
        VpnController.getInstance().start(getContext());
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("intro_button_click",true);
        startActivity(intent);
        getActivity().finish();
      }
    });

    isShown = true;

    return welcomeView;
  }

  public static class Page extends Fragment {
    private static final String IMAGE = "image";
    private static final String HEADLINE = "headline";
    private static final String BODY = "body";

    static Page newInstance(@DrawableRes int image, @StringRes int headline, @StringRes int body) {
      Page page = new Page();
      Bundle arguments = new Bundle(3);
      arguments.putInt(IMAGE, image);
      arguments.putInt(HEADLINE, headline);
      arguments.putInt(BODY, body);
      page.setArguments(arguments);
      return page;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.intro_page, container, false);

      ImageView imageView = view.findViewById(R.id.intro_image);
      imageView.setImageResource(getArguments().getInt(IMAGE));

      TextView headlineView = view.findViewById(R.id.intro_headline);
      headlineView.setText(getArguments().getInt(HEADLINE));

      TextView bodyView = view.findViewById(R.id.intro_body);
      bodyView.setMovementMethod(LinkMovementMethod.getInstance());
      bodyView.setText(getArguments().getInt(BODY));

      final Button intro_button = view.findViewById(R.id.button_intro);
      intro_button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          PersistentState.setWelcomeApproved(getContext(), true);

          MainActivity.login = 0;
          VpnController.getInstance().start(getContext());
          Intent intent = new Intent(getActivity(), MainActivity.class);
          intent.putExtra("intro_button_click",true);
          startActivity(intent);
          getActivity().finish();
        }
      });
      return view;
    }
  }

  private static class Adapter extends FragmentStateAdapter {
    private final int[] images = {
        R.drawable.ad_block,
        R.drawable.private_policy,
        R.drawable.active_control

    };
    private final int[] headlines = {
        R.string.intro_benefit_headline,
        R.string.intro2,
        R.string.intro3
    };
    private final int[] text = {
        R.string.intro_benefit_body,
        R.string.detail_intro2,
        R.string.detail_intro3
    };

    private final Fragment[] pages;
    Adapter(Fragment fragment) {
      super(fragment);
      pages = new Fragment[NUM_PAGES];
      for (int i = 0; i < pages.length; ++i) {
        pages[i] = Page.newInstance(images[i], headlines[i], text[i]);
      }
    }

    @Override
    public int getItemCount() {
      return pages.length;
    }

    @Override
    public @NonNull Fragment createFragment(int position) {
      return pages[position];
    }
  }

  private static class ButtonVisibilityUpdater extends ViewPager2.OnPageChangeCallback {
    private final Button backButton, nextButton, acceptButton;

    ButtonVisibilityUpdater(Button backButton, Button nextButton, Button acceptButton) {
      this.backButton = backButton;
      this.nextButton = nextButton;
      this.acceptButton = acceptButton;
    }

    @Override
    public void onPageSelected(int position) {
      super.onPageSelected(position);
      backButton.setVisibility(position == 0 ? View.GONE : View.GONE);
      nextButton.setVisibility(position == NUM_PAGES - 1 ? View.GONE : View.GONE);
      acceptButton.setVisibility(position == NUM_PAGES - 1 ? View.GONE : View.GONE);
    }
  }
}
