package me.StevenLawson.BukkitTelnetClient;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class PlayerInfo
{
    public static int numColumns;
    public static String[] columnNames;

    private final String name;
    private final String ip;
    private final String displayName;
    private final String uuid;

    // TFM tags:
    private final boolean admin;
    private final boolean telnetAdmin;
    private final boolean seniorAdmin;
    private final String tag;
    private final String nickName;

    static
    {
        final Map<Integer, String> columnNamesMap = new HashMap<>();

        int _numColumns = 0;
        final Method[] declaredMethods = PlayerInfo.class.getDeclaredMethods();
        for (final Method method : declaredMethods)
        {
            final Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
            for (final Annotation annotation : declaredAnnotations)
            {
                if (annotation instanceof PlayerTableColumn)
                {
                    PlayerTableColumn playerInfoTag = (PlayerTableColumn) annotation;
                    columnNamesMap.put(playerInfoTag.column(), playerInfoTag.name());
                    _numColumns++;
                }
            }
        }

        final String[] _columnNames = new String[_numColumns];
        for (int i = 0; i < _numColumns; i++)
        {
            _columnNames[i] = columnNamesMap.get(i);
        }

        columnNames = _columnNames;
        numColumns = _numColumns;
    }

    public PlayerInfo(String name, String ip, String displayName, String uuid, boolean admin, boolean telnetAdmin, boolean seniorAdmin, String tag, String nickName)
    {
        this.name = name;
        this.ip = ip;
        this.displayName = displayName;
        this.uuid = uuid;
        this.admin = admin;
        this.telnetAdmin = telnetAdmin;
        this.seniorAdmin = seniorAdmin;
        this.tag = tag;
        this.nickName = nickName;
    }

    @PlayerTableColumn(name = "Name", column = 0)
    public String getName()
    {
        return name;
    }

    @PlayerTableColumn(name = "IP", column = 1)
    public String getIp()
    {
        return ip;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getUuid()
    {
        return uuid;
    }

    public boolean isAdmin()
    {
        return admin;
    }

    public boolean isTelnetAdmin()
    {
        return telnetAdmin;
    }

    public boolean isSeniorAdmin()
    {
        return seniorAdmin;
    }

    @PlayerTableColumn(name = "Tag", column = 2)
    public String getTag()
    {
        return tag == null || tag.isEmpty() || tag.equalsIgnoreCase("null") ? "" : tag;
    }

    @PlayerTableColumn(name = "Nickname", column = 3)
    public String getNickName()
    {
        return nickName == null || nickName.isEmpty() || nickName.equalsIgnoreCase("null") ? "" : nickName;
    }

    @PlayerTableColumn(name = "Admin Level", column = 4)
    public String getAdminLevel()
    {
        if (isAdmin())
        {
            if (isSeniorAdmin())
            {
                return "Senior";
            }
            else if (isTelnetAdmin())
            {
                return "Telnet";
            }
            else
            {
                return "Super";
            }
        }
        return "";
    }

    public String getColumnValue(int columnIndex)
    {
        final Method[] declaredMethods = this.getClass().getDeclaredMethods();
        for (final Method method : declaredMethods)
        {
            final Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
            for (final Annotation annotation : declaredAnnotations)
            {
                if (annotation instanceof PlayerTableColumn)
                {
                    PlayerTableColumn playerInfoTag = (PlayerTableColumn) annotation;

                    if (playerInfoTag.column() == columnIndex)
                    {
                        try
                        {
                            final Object value = method.invoke(this);
                            if (value != null)
                            {
                                return value.toString();
                            }
                        }
                        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
                        {
                            BukkitTelnetClient.LOGGER.log(Level.SEVERE, null, ex);
                        }

                        return "null";
                    }
                }
            }
        }

        return "null";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface PlayerTableColumn
    {
        public String name();

        public int column();
    }

    public static Comparator<PlayerInfo> getComparator()
    {
        return new Comparator<PlayerInfo>()
        {
            @Override
            public int compare(PlayerInfo a, PlayerInfo b)
            {
                return a.getName().compareTo(b.getName());
            }
        };
    }
}
