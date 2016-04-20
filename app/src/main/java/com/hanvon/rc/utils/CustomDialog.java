package com.hanvon.rc.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hanvon.rc.R;


public class CustomDialog extends Dialog {
	
	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomDialog(Context context) {
		super(context);
	}

	/**
	 * Helper class for creating a custom dialog
	 */
	public static class Builder {

		private Context context;
		private int mIcon;
		private String title;
		private String message;
		private String positiveButtonText;
		private String negativeButtonText;
		private String neutralButtonText;
		private TextView positiveButton, negativeButton, neutralButton;
		private LinearLayout buttonLL;
		private View contentView;
		private TextView mTitleView;
		// 对话框content的类型，如单选列表；多选列表；列表等
		private int contentType = -1;
		private static final int SINGLE_CHOICE_LIST = 0;
		private static final int MULTIPE_CHOICE_LIST = 1;
		private static final int NO_CHOICE_LIST = 2;
		// 单选列表数组
		private int mSingleChoiceItemsId = -1;
		// 单选列表被选中的item
		private int mSingleChoiceCheckedId = -1;
		private String[] mSingleChoiceItemsArray = null;
		// 列表数组 int
		private int mNoChoiceList = -1;
		// 列表数组 String[]
		private String[] sNoChoiceList = null;
		private OnClickListener positiveButtonClickListener,
				neutralButtonClickListener, negativeButtonClickListener,
				singleChoiceItemsCheckListener, noChoiceListonClickListener;
		private OnCancelListener cancelListener;
		private boolean PositiveButtonAutoDismiss = true,
				NegativeButtonAutoDismiss = true,
				NeutralButtonAutoDismiss = true;
		private boolean flag;
		
		public Builder(Context context) {
			this.context = context;
		}
		
		public Builder(Context context,boolean flag) {
			this.context = context;
			this.flag = flag;
		}

		public Builder setIcon(int icon) {
			this.mIcon = icon;
			return this;
		}

		/**
		 * Set the Dialog message from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder reSetTitle(String title) {
			this.title = title;
			if (mTitleView != null) {
				mTitleView.setText(title);
			}
			return this;
		}

		/**
		 * Set a custom content view for the Dialog. If a message is set, the
		 * contentView is not added to the Dialog...
		 * 
		 * @param v
		 * @return
		 */
		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText,
				OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			if (listener == null) {
				this.positiveButtonClickListener = new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// 无实现

					}
				};
			} else {
				this.positiveButtonClickListener = listener;
			}
			return this;
		}

		/**
		 * Set the positive button text and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(String positiveButtonText,
				OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			if (listener == null) {
				this.positiveButtonClickListener = new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// 无实现

					}
				};
			} else {
				this.positiveButtonClickListener = listener;
			}
			return this;
		}

		/**
		 * Set the positive button text and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @param autoDismiss
		 *            自动关闭dialog
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText,
				OnClickListener listener, boolean autoDismiss) {
			PositiveButtonAutoDismiss = autoDismiss;
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			if (listener == null) {
				this.positiveButtonClickListener = new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// 无实现

					}
				};
			} else {
				this.positiveButtonClickListener = listener;
			}
			return this;
		}

		/**
		 * Set the negative button resource and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(int negativeButtonText,
				OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			if (listener == null) {
				this.negativeButtonClickListener = new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// 无实现

					}
				};
			} else {
				this.negativeButtonClickListener = listener;
			}
			return this;
		}

		/**
		 * Set the negative button text and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(String negativeButtonText,
				OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			if (listener == null) {
				this.negativeButtonClickListener = new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// 无实现

					}
				};
			} else {
				this.negativeButtonClickListener = listener;
			}
			return this;
		}

		/**
		 * Set the negative button resource and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @param autoDismiss
		 *            自动关闭dialog
		 * @return
		 */
		public Builder setNegativeButton(int negativeButtonText,
				OnClickListener listener, boolean autoDismiss) {
			NegativeButtonAutoDismiss = autoDismiss;
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			if (listener == null) {
				this.negativeButtonClickListener = new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// 无实现
					}
				};
			} else {
				this.negativeButtonClickListener = listener;
			}
			return this;
		}

		public Builder setNeutralButton(int neutralButtonText,
				OnClickListener listener) {
			this.neutralButtonText = (String) context
					.getText(neutralButtonText);
			if (listener == null) {
				this.neutralButtonClickListener = new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// 无实现
					}
				};
			} else {
				this.neutralButtonClickListener = listener;
			}
			return this;
		}

		public Builder setNeutralButton(String neutralButtonText,
				OnClickListener listener) {
			this.neutralButtonText = neutralButtonText;
			if (listener == null) {
				this.neutralButtonClickListener = new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// 无实现

					}
				};
			} else {
				this.neutralButtonClickListener = listener;
			}
			return this;
		}

		/**
		 * 
		 * @param neutralButtonText
		 * @param listener
		 * @param autoDismiss
		 *            自动关闭dialog
		 * @return
		 */
		public Builder setNeutralButton(int neutralButtonText,
				OnClickListener listener, boolean autoDismiss) {
			NeutralButtonAutoDismiss = autoDismiss;
			this.neutralButtonText = (String) context
					.getText(neutralButtonText);
			if (listener == null) {
				this.neutralButtonClickListener = new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// 无实现
					}
				};
			} else {
				this.neutralButtonClickListener = listener;
			}
			return this;
		}

		// 无选择列表
		public Builder setItems(int itemsId, OnClickListener listener) {
			this.contentType = NO_CHOICE_LIST;
			this.mNoChoiceList = itemsId;
			if (listener == null) {
				this.noChoiceListonClickListener = new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// 无实现

					}
				};
			} else {
				this.noChoiceListonClickListener = listener;
			}
			return this;
		}

		// 无选择列表
		public Builder setItems(String[] itemsId, OnClickListener listener) {
			this.contentType = NO_CHOICE_LIST;
			this.sNoChoiceList = itemsId;
			if (listener == null) {
				this.noChoiceListonClickListener = new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// 无实现

					}
				};
			} else {
				this.noChoiceListonClickListener = listener;
			}
			return this;
		}

		// wu0wu 2012 03-15 实现单选列表
		public Builder setSingleChoiceItems(int singleChoiceItemsId,
				int singleChoiceCheckedId, OnClickListener listener) {
			this.contentType = SINGLE_CHOICE_LIST;
			this.mSingleChoiceItemsId = singleChoiceItemsId;
			this.mSingleChoiceCheckedId = singleChoiceCheckedId;
			if (listener == null) {
				this.singleChoiceItemsCheckListener = new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// 无实现

					}
				};
			} else {
				this.singleChoiceItemsCheckListener = listener;
			}
			return this;
		}

		public Builder setSingleChoiceItems(String[] singleChoiceItems,
				int singleChoiceCheckedId, OnClickListener listener) {
			this.contentType = SINGLE_CHOICE_LIST;
			this.mSingleChoiceItemsArray = singleChoiceItems;
			this.mSingleChoiceCheckedId = singleChoiceCheckedId;
			if (listener == null) {
				this.singleChoiceItemsCheckListener = new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// 无实现

					}
				};
			} else {
				this.singleChoiceItemsCheckListener = listener;
			}
			return this;
		}

		public Builder setOnCancelListener(OnCancelListener onCancelListener) {
			this.cancelListener = onCancelListener;
			return this;
		}

		public Builder setPositiveButtonVisible(boolean isVisible) {
			if (positiveButton != null) {
				if (isVisible) {
					positiveButton.setVisibility(View.VISIBLE);
				} else {
					positiveButton.setVisibility(View.GONE);
				}
			}
			return this;
		}

		public Builder setNeutralButtonVisible(boolean isVisible) {
			if (neutralButton != null) {
				if (isVisible) {
					neutralButton.setVisibility(View.VISIBLE);
				} else {
					neutralButton.setVisibility(View.GONE);
				}
			}
			return this;
		}

		public Builder setNegativeButtonVisible(boolean isVisible) {
			if (negativeButton != null) {
				if (isVisible) {
					negativeButton.setVisibility(View.VISIBLE);
				} else {
					negativeButton.setVisibility(View.GONE);
				}
			}
			return this;
		}

		public Builder setButtonVisible(boolean isVisible) {
			if (buttonLL != null) {
				if (isVisible) {
					buttonLL.setVisibility(View.VISIBLE);
				} else {
					buttonLL.setVisibility(View.GONE);
				}
			}
			return this;
		}

		/**
		 * Create the custom dialog
		 */


		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context,
					R.style.Dialog);
			View layout = inflater.inflate(R.layout.dialog, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			ImageView icon = (ImageView) layout.findViewById(R.id.icon);
			if (mIcon != 0) {
				icon.setVisibility(View.VISIBLE);
				icon.setImageResource(mIcon);
			} else {
				icon.setVisibility(View.GONE);
			}
			buttonLL = (LinearLayout) layout.findViewById(R.id.button);
			// set the dialog title
			mTitleView = ((TextView) layout.findViewById(R.id.title));
			mTitleView.setText(title);
			// set the confirm button
			if (positiveButtonText != null) {
				positiveButton = ((TextView) layout
						.findViewById(R.id.positiveButton));
				positiveButton.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((TextView) layout.findViewById(R.id.positiveButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
									if (PositiveButtonAutoDismiss)
										dialog.dismiss();
								}
							});
				}
			} else {
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}
			// set the neutral button
			if (neutralButtonText != null) {
				neutralButton = ((TextView) layout
						.findViewById(R.id.neutralButton));
				neutralButton.setText(neutralButtonText);
				if (neutralButtonClickListener != null) {
					((TextView) layout.findViewById(R.id.neutralButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									neutralButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEUTRAL);
									if (NeutralButtonAutoDismiss)
										dialog.dismiss();
								}
							});
				}
			} else {
				layout.findViewById(R.id.neutralButton)
						.setVisibility(View.GONE);
				layout.findViewById(R.id.negativeButton_line).setVisibility(View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				negativeButton = ((TextView) layout
						.findViewById(R.id.negativeButton));
				negativeButton.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((TextView) layout.findViewById(R.id.negativeButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
									if (NegativeButtonAutoDismiss)
										dialog.dismiss();
								}
							});
				}
			} else {
				layout.findViewById(R.id.negativeButton).setVisibility(
						View.GONE);
				layout.findViewById(R.id.neutralButton_line).setVisibility(View.GONE);
			}
			if(positiveButtonText==null&&negativeButtonText==null&&neutralButtonText==null){
				layout.findViewById(R.id.button).setVisibility(
						View.GONE);
			}
			// set the content message
			if (message != null)
			{
				((TextView) layout.findViewById(R.id.message)).setText(message);
			}
			else if (contentType == SINGLE_CHOICE_LIST) { // singleChioceitem
				Log.i("CustomDialog", "contentType==SINGLE_CHOICE_LIST");
				//listItemSingleChoiceMode(layout, dialog);
			}
			else if (contentType == NO_CHOICE_LIST) {
				//listItemNoChoiceList(layout, dialog);
			}
			else if (contentView != null) {
				// if no message set
				// add the contentView to the dialog body
				((LinearLayout) layout.findViewById(R.id.content))
						.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.content)).addView(
						contentView, new LayoutParams(LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT));
				contentView.setPadding(20, 0, 20, 0);
			}
			dialog.setContentView(layout);
			return dialog;
		}
		public CustomDialog create(boolean needWallpaper) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog;
			if(needWallpaper){
				dialog = new CustomDialog(context,
						R.style.DialogWallpaper);
			}else{
				dialog = new CustomDialog(context,
						R.style.Dialog);
			}
			View layout = inflater.inflate(R.layout.dialog, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			ImageView icon = (ImageView) layout.findViewById(R.id.icon);
			if (mIcon != 0) {
				icon.setVisibility(View.VISIBLE);
				icon.setImageResource(mIcon);
			} else {
				icon.setVisibility(View.GONE);
			}
			buttonLL = (LinearLayout) layout.findViewById(R.id.button);
			// set the dialog title
			mTitleView = ((TextView) layout.findViewById(R.id.title));
			mTitleView.setText(title);
			// set the confirm button
			if (positiveButtonText != null) {
				positiveButton = ((TextView) layout
						.findViewById(R.id.positiveButton));
				positiveButton.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((TextView) layout.findViewById(R.id.positiveButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
									if (PositiveButtonAutoDismiss)
										dialog.dismiss();
								}
							});
				}
			} else {
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}
			// set the neutral button
			if (neutralButtonText != null) {
				neutralButton = ((TextView) layout
						.findViewById(R.id.neutralButton));
				neutralButton.setText(neutralButtonText);
				if (neutralButtonClickListener != null) {
					((TextView) layout.findViewById(R.id.neutralButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									neutralButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEUTRAL);
									if (NeutralButtonAutoDismiss)
										dialog.dismiss();
								}
							});
				}
			} else {
				layout.findViewById(R.id.neutralButton)
						.setVisibility(View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				negativeButton = ((TextView) layout
						.findViewById(R.id.negativeButton));
				negativeButton.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((TextView) layout.findViewById(R.id.negativeButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
									if (NegativeButtonAutoDismiss)
										dialog.dismiss();
								}
							});
				}
			} else {
				layout.findViewById(R.id.negativeButton).setVisibility(
						View.GONE);
			}
			// set the content message
			if (message != null) {
				((TextView) layout.findViewById(R.id.message)).setText(message);
			} else if (contentType == SINGLE_CHOICE_LIST) { // singleChioceitem
				//listItemSingleChoiceMode(layout, dialog);
			} else if (contentType == NO_CHOICE_LIST) {
				//listItemNoChoiceList(layout, dialog);
			} else if (contentView != null) {
				// if no message set
				// add the contentView to the dialog body
				((LinearLayout) layout.findViewById(R.id.content))
						.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.content)).addView(
						contentView, new LayoutParams(LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT));
				contentView.setPadding(20, 0, 20, 0);
			}
			dialog.setContentView(layout);
			return dialog;
		}
		/*
	private void listItemNoChoiceList(View layout, final CustomDialog dialog) {
            String[] listContent = null;
            if (sNoChoiceList == null) {
                listContent = context.getResources().getStringArray(mNoChoiceList);
            } else {
                listContent = sNoChoiceList;
            }
            if (listContent != null) {
                ListView listView = getSingleChoiceListView((SingleChoiceListAdapter) createAdapter(listContent, false));
                layout.findViewById(R.id.button).setVisibility(View.GONE);
                addViewToParentLayout(layout, listView);
                listView.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (noChoiceListonClickListener != null) {
                            noChoiceListonClickListener.onClick(dialog, position);
                            dialog.dismiss();
                        }
                    }
                });
            }

        }
		private Adapter createAdapter(String[] listContent, boolean isVisibleChoiceImg) {
            ArrayList<Map<String, Object>> contentList = new ArrayList<Map<String, Object>>();
            for (String aListContent : listContent) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("title", aListContent);
                contentList.add(map);
            }
            return new SingleChoiceListAdapter(context, isVisibleChoiceImg, contentList);
        }
        */
		
		 private void addViewToParentLayout(View parent, View subView) {
	            ((LinearLayout) parent.findViewById(R.id.content)).removeAllViews();
	            ((LinearLayout) parent.findViewById(R.id.content)).addView(subView, new LayoutParams(
	                            LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	        }
		
		private ListView getSingleChoiceListView(BaseAdapter adapter) {
            ListView listView = new ListView(context);
            listView.setCacheColorHint(context.getResources().getColor(R.color.transparent_background));
            listView.setAdapter(adapter);
            listView.setItemsCanFocus(false);
            return listView;
        }

		// wu0wu单选列表
		private void listItemSingleChoiceMode(View layout,
				final CustomDialog dialog) {
			ArrayAdapter<String> arrayAdapter = null;
			if (mSingleChoiceItemsArray != null) {
				arrayAdapter = new ArrayAdapter<String>(context,
						android.R.layout.simple_list_item_single_choice,
						mSingleChoiceItemsArray);
			} else {
				arrayAdapter = new ArrayAdapter<String>(context,
						android.R.layout.simple_list_item_single_choice,
						context.getResources().getStringArray(
								mSingleChoiceItemsId));
			}

			ListView list = new ListView(context);
			list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			list.setAdapter(arrayAdapter);
			list.setItemsCanFocus(false);
			list.setItemChecked(mSingleChoiceCheckedId, true);

			((LinearLayout) layout.findViewById(R.id.content)).removeAllViews();
			((LinearLayout) layout.findViewById(R.id.content)).addView(list,
					new LayoutParams(LayoutParams.FILL_PARENT,
							LayoutParams.WRAP_CONTENT));
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if (singleChoiceItemsCheckListener != null) {
						singleChoiceItemsCheckListener
								.onClick(dialog, position);
						// Log.i("wu0wu","singleChoiceItemsCheckListener onItemClick="
						// + position);
					}
				}
			});
		}

		public CustomDialog show() {
			CustomDialog dialog ;
			if (flag){
				dialog = CustomDialogcreate();
			}else{
				dialog = create();
			}
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			flag = false;
			return dialog;
		}
		
		
		/*************************************************************************/
		public CustomDialog CustomDialogcreate() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context,
					R.style.Dialog);
			View layout = inflater.inflate(R.layout.cusrom_dialog, null);
		//	dialog.addContentView(layout, new LayoutParams(
			//		LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			// set the content message
			if (message != null) {
				((TextView) layout.findViewById(R.id.message1)).setText(message);
			} 
			dialog.setContentView(layout);
			dialog.setCanceledOnTouchOutside(false);
			return dialog;
		}
	}

}
