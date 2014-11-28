package tudoku.com.tudoku.app.attractions;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import tudoku.com.tudoku.R;

public class AttractionPagerAdapter extends FragmentPagerAdapter {

    protected static final int POSITION_DESCRIPTION = 0;
    protected static final int POSITION_MAP = 1;
    protected static final int POSITION_COMMENTS = 2;

    private Context mContext;
    private Bundle mFragmentArgs;

    public AttractionPagerAdapter(FragmentManager fm, Context context, Bundle fragmentArgs) {
        super(fm);
        mContext = context;
        mFragmentArgs = fragmentArgs;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case POSITION_DESCRIPTION:
                fragment = new AttractionDescriptionFragment();
                break;
            case POSITION_MAP:
                fragment = new AttractionMapFragment();
                break;
            case POSITION_COMMENTS:
            default:
                throw new IllegalArgumentException("Wrong position: " + position);
        }

        fragment.setArguments(mFragmentArgs);

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case POSITION_DESCRIPTION:
                return mContext.getString(R.string.attraction_description);
            case POSITION_MAP:
                return mContext.getString(R.string.attraction_map);
            case POSITION_COMMENTS:
                return mContext.getString(R.string.attraction_comments);
            default:
                throw new IllegalArgumentException("Wrong position: " + position);
        }
    }
}
