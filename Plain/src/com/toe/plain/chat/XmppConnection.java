package com.toe.plain.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;

import com.toe.plain.ConversationsListItem;
import com.toe.plain.ConversationsListItemAdapter;

public class XmppConnection {
	public static final String TYPE_NORMAL = "chat_message_normal";
	static Context ctx;
	static AbstractXMPPConnection conn2;
	List<Message> offline_list;
	public static LinkedHashMap<String, String> message_map = new LinkedHashMap<String, String>();
	int is_connected = 0;

	String service_name = "bmo.com";
	String server = "103.3.63.131"; // "45.33.64.234"
	int port = 5222;

	static Handler handle_messages, handle_conversations;
	String event, sender_name;
	static ConversationsListItemAdapter adapt_messages, adapt_conversations;

	JSONObject received_message_json;
	static JSONObject send_message_json;
	ArrayList<JSONObject> messages_list = new ArrayList<JSONObject>();
	Context connection_context;
	String connection_key1, connection_key2;
	static int reg_status;
	static Time time = new Time();
	public static ArrayList<String> track_conversations = new ArrayList<String>();
	public static ArrayList<String> track_messages = new ArrayList<String>();

	static ArrayList<ConversationsListItem> messages_string = new ArrayList<ConversationsListItem>();
	static ArrayList<ConversationsListItem> conversations_string = new ArrayList<ConversationsListItem>();

	public void listenforIncoming() {

		StanzaFilter filter = MessageTypeFilter.CHAT;

		StanzaListener listen = new StanzaListener() {

			@Override
			public void processPacket(Stanza arg0) throws NotConnectedException {
				// TODO Auto-generated method stub

				Message msg = (Message) arg0;
				Log.d("the packet body", msg.getBody());
				jsonFromReceivedPacket(msg.getBody(), msg.getFrom());

			}
		};

		conn2.addSyncStanzaListener(listen, filter);

	}

	public boolean checkDataConnection(Context ctx) {

		ConnectivityManager connec = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobile = connec
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		return wifi.isConnected() || mobile.isConnected();

	}

	public static void checkRegistration(Context context, String key1,
			String key2) {

		// reg status from shared prefs. int reg_status;

		SharedPreferences sp = context.getSharedPreferences(
				context.getPackageName(), Context.MODE_PRIVATE);

		sp.getInt("reg_status", 0);

		if (reg_status == 1) {
			Log.d("account exists", "user already registered");
		} else {
			AccountManager accountManager = AccountManager.getInstance(conn2);
			AccountManager
					.sensitiveOperationOverInsecureConnectionDefault(true);
			try {
				try {
					accountManager.createAccount(key1, key2);
				} catch (NoResponseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("xmpp account manager", e.toString());
				} catch (NotConnectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("xmpp account manager", e.toString());
				}
			} catch (XMPPException e) {
				Log.d("xmpp account manager", e.toString());
			}
			sp.edit().putInt("reg_status", 1).commit();
		}
	}

	public void createConnection(final Context context, final String key1,
			final String key2) {

		connection_context = context;
		connection_key1 = key1;
		connection_key2 = key2;

		XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration
				.builder().setUsernameAndPassword(key1, key2)
				.setServiceName(service_name).setHost(server).setPort(5222)
				.setConnectTimeout(10000)
				.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
				.setDebuggerEnabled(true).build();

		conn2 = new XMPPTCPConnection(config);

		conn2.addConnectionListener(new ConnectionListener() {

			@Override
			public void reconnectionSuccessful() {
				// TODO Auto-generated method stub
				listenforIncoming();
			}

			@Override
			public void reconnectionFailed(Exception arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void reconnectingIn(int arg0) {
				// TODO Auto-generated method stub
				System.out.println(String.valueOf(arg0));
			}

			@Override
			public void connectionClosedOnError(Exception arg0) {
				// TODO Auto-generated method stub
				createConnection(context, key1, key2);
			}

			@Override
			public void connectionClosed() {
				// TODO Auto-generated method stub
				createConnection(context, key1, key2);
			}

			@Override
			public void connected(XMPPConnection arg0) {
				// TODO Auto-generated method stub
				Log.e("connected", "user connected to server");
				listenforIncoming();
				is_connected = 1;
			}

			@Override
			public void authenticated(XMPPConnection arg0, boolean arg1) {
				// TODO Auto-generated method stub
				Log.e("connected", "user authenticated");

			}
		});

		connectToServer(context);
		checkRegistration(context, key1, key2);
		loginToServer();

	}

	public void connectToServer(Context context) {

		while (true) {

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (checkDataConnection(context)) {

				try {
					if (conn2.isConnected() == false) {
						conn2.connect();
					}

					break;
					// client already connected and client already logged in
					// exceptions
				} catch (SmackException e) {
					Log.e("smack error connecting to server", e.toString());

				} catch (IOException e) {
					Log.e("io error connecting to server", e.toString());

				} catch (XMPPException e) {
					Log.e("xmpp error connecting to server", e.toString());

				}
			}
		}
	}

	public void loginToServer() {

		while (true) {
			try {
				if (conn2.isAuthenticated() == false) {
					conn2.login();// not effective. fix

				}
				break;
			} catch (XMPPException e) {
				Log.e("not logged in", e.toString());

			} catch (SmackException e) {
				// loginToServer();
				// TODO Auto-generated catch block
				Log.e("not logged in", e.toString());

			} catch (IOException e) {
				Log.e("not logged in", e.toString());

			}

		}

		handleOfflineMessages();

	}

	public void handleOfflineMessages() {
		OfflineMessageManager off = new OfflineMessageManager(conn2);
		try {
			off.supportsFlexibleRetrieval();
		} catch (NoResponseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (XMPPErrorException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NotConnectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			offline_list = off.getMessages();
		} catch (NoResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMPPErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Message msg : offline_list) {

			jsonFromReceivedPacket(msg.getBody(), msg.getFrom());
		}
	}

	public static boolean sendMessage(String event, String recepient_id,
			String message_body) {

		try {
			send_message_json = new JSONObject();
			send_message_json.put("event", event);
			send_message_json.put("message", message_body);

		} catch (JSONException e1) {
			Log.e("error parsing send message json", e1.toString());
		}
		Message newMessage = new Message();
		newMessage.setType(Message.Type.chat);
		newMessage.setBody(send_message_json.toString());
		newMessage.setTo(recepient_id + "@bmo.com");
		newMessage.setFrom(conn2.getUser());
		try {
			if (conn2 != null) {
				conn2.sendStanza(newMessage);
			} else {
				Log.e("cannot send message", "no connection");
			}

		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			// cache and retry sending here
			Log.e("unable to send message", e.toString());

			return false;
		}

		message_map.put(recepient_id + Long.toString(time.toMillis(false)),
				message_body);

		String message = recepient_id;
		android.os.Message msg = android.os.Message.obtain();
		msg.obj = message;
		handle_messages.sendMessage(msg);

		if (track_conversations.contains(recepient_id)) {
			Log.d("existing item", "item already exists ");

		} else {
			conversations_string.add(new ConversationsListItem(recepient_id));
			track_conversations.add(recepient_id);
		}

		if (handle_conversations != null) {
			handle_conversations.sendEmptyMessage(0);

		}

		if (event.equals("initiate_chat")) {

		}
		return true;

	}

	public void jsonFromReceivedPacket(String message_packet, String sender) {

		try {
			Log.d("escaped string",
					StringEscapeUtils.unescapeXml(StringEscapeUtils
							.unescapeHtml(message_packet)));
			received_message_json = new JSONObject(
					StringEscapeUtils.unescapeXml(StringEscapeUtils
							.unescapeHtml(message_packet)));
		} catch (JSONException e) {
			Log.e("error parsing received_message string", e.toString());
		}

		try {
			event = received_message_json.getString("event");
		} catch (JSONException e) {
			Log.e("error parsing event type", e.toString());
		}

		if (event.equals("initiate_chat")) {

			sender_name = sender.replace("@bmo.com/Smack", "");

			if (track_conversations.contains(sender_name)) {
				Log.d("existing item", "item already exists ");

			} else {
				conversations_string
						.add(new ConversationsListItem(sender_name));
				track_conversations.add(sender_name);
			}
																																																																																																																																																																																																																									
			if (handle_conversations != null) {																																			
				handle_conversations.sendEmptyMessage(0);																																																																																																																																																																	
				Log.d("initiate chat with ", sender);
			}

		} else if (event.equals("chat_message_normal")) {

			try {																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																		
																
				
				time.setToNow();
				sender_name = sender.replace("@bmo.com/Smack", "");
																																																																																																																																																																																																
				String the_message = received_message_json.getString("message");
				time.setToNow();
				message_map.put(
						sender_name + Long.toString(time.toMillis(false)),
						the_message);

				track_messages.add(the_message);

				messages_string.add(new ConversationsListItem(
						received_message_json.getString("message")));
				Notify.showNotification(ctx,
						"New message from  " + sender_name, sender_name);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (handle_messages != null) {

				String message = sender_name;
				android.os.Message msg = android.os.Message.obtain();
				msg.obj = message;
				handle_messages.sendMessage(msg);
				// msg.setTarget(handle_messages);
				// msg.sendToTarget();
			}

		} else if (event.equals("chat_message_image")) {
			Log.d("chat image received from", sender);
		}

	}

	private static boolean isMyServiceRunning(Class<?> serviceClass,
			Context context) {

		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static void initConversations(Context context, Handler handler,
			final ConversationsListItemAdapter adapter,
			ArrayList<ConversationsListItem> messages, String key1, String key2) {

		ctx = context;
		handler = new Handler() {

			public void handleMessage(android.os.Message msg) {
				final int what = msg.what;
				switch (what) {
				case 0://
					adapter.notifyDataSetChanged();
					// list.invalidate();
					Log.d("handle received", "handle received");
					break;
				case 1:

					break;
				}
			}
		};
		adapt_conversations = adapter;
		handle_conversations = handler;
		conversations_string = messages;

		if (isMyServiceRunning(Master.class, context) == true) {
			Log.d("running service", "the service is already running");
		} else {

			Bundle bundle = new Bundle();

			bundle.putString("key1", key1);
			bundle.putString("key2", key2);
			Intent intent = new Intent(context, Master.class);
			intent.putExtras(bundle);
			context.startService(intent);
		}

	}

	public static void initMessages(Context context, Handler handler,
			final ConversationsListItemAdapter adapter,
			ArrayList<ConversationsListItem> messages,
			final ArrayList<ConversationsListItem> the_messages,
			final String thread_name) {

		handler = new Handler() {

			public void handleMessage(android.os.Message msg) {

				String sender = (String) msg.obj;
				populateSelectedConversation(sender, the_messages, thread_name);
				adapter.notifyDataSetChanged();
				// list.invalidate();
				Log.d("handle received", sender);

			}
		};
		adapt_messages = adapter;
		handle_messages = handler;
		messages_string = messages;
	}

	public static ArrayList<ConversationsListItem> populateSelectedConversation(
			String sender, ArrayList<ConversationsListItem> the_messages,
			String thread_name) {

		for (Map.Entry<String, String> entry : message_map.entrySet()) {

			if (entry.getKey().contains(thread_name)) {

				if (!the_messages.contains(new ConversationsListItem(entry
						.getValue()))) {
					the_messages
							.add(new ConversationsListItem(entry.getValue()));
				}
			}

		}
		return the_messages;

	}

}
