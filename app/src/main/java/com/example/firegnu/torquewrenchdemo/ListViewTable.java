package com.example.firegnu.torquewrenchdemo;

/**
 * Created by firegnu on 15-1-22.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.LinkedList;
import java.util.List;


public class ListViewTable extends LinearLayout {
    public ListViewTable( Context context ) {
        super( context );
    }

    public ListViewTable(Context context, AttributeSet attrs) {
        super( context, attrs );
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout( changed, l, t, r, b );

        List<Integer> colWidths = new LinkedList<Integer>();

        TableLayout header = (TableLayout) findViewById( R.id.headerTable );
        TableLayout body = (TableLayout) findViewById( R.id.bodyTable );
        if (body.getChildCount() > 0) {

            for ( int rownum = 0; rownum < 1; rownum++ ) {
                TableRow row = (TableRow) body.getChildAt( rownum );
                for ( int cellnum = 0; cellnum < row.getChildCount(); cellnum++ ) {
                    View cell = row.getChildAt( cellnum );
                    TableRow.LayoutParams params = (TableRow.LayoutParams)cell.getLayoutParams();
                    Integer cellWidth = params.span == 1 ? cell.getWidth() : 0;
                    if ( colWidths.size() <= cellnum ) {
                        colWidths.add( cellWidth );
                    } else {
                        Integer current = colWidths.get( cellnum );
                        if( cellWidth > current ) {
                            colWidths.remove( cellnum );
                            colWidths.add( cellnum, cellWidth );
                        }
                    }
                }
            }

            for ( int rownum = 0; rownum < header.getChildCount(); rownum++ ) {
                TableRow row = (TableRow) header.getChildAt( rownum );
                for ( int cellnum = 0; cellnum < row.getChildCount(); cellnum++ ) {
                    View cell = row.getChildAt( cellnum );
                    TableRow.LayoutParams params = (TableRow.LayoutParams)cell.getLayoutParams();
                    params.width = 0;
                    for( int span = 0; span < params.span; span++ ) {
                        params.width += colWidths.get( cellnum + span );
                    }
                    //cell.setLayoutParams(params);
                }
            }
        }
    }
}
