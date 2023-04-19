package me.alek.security.event;

import lombok.Getter;
import me.alek.AntiMalwarePlugin;
import me.alek.security.SecurityManager;
import me.alek.security.event.wrappers.WrappedMethodRegisteredListener;
import me.alek.security.event.wrappers.WrappedUniqueRegisteredListener;
import me.alek.utils.JARFinder;
import me.alek.utils.ZipUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChatExecutorBlocker extends AbstractListener {

    public ChatExecutorBlocker(SecurityManager manager) {
        super(manager);
        this.manager = manager;

        final CancellationEventProxy<AsyncPlayerChatEvent> eventProxy = new CancellationEventProxy<>(AsyncPlayerChatEvent.class, true);
        eventProxy.addListener(new CancellationEventProxy.CancelListener<AsyncPlayerChatEvent>() {
            @Override
            public void onCancelled(RegisteredListener registeredListener, AsyncPlayerChatEvent event) {
                final PluginListener pluginListener = new PluginListener() {
                    @Override
                    public boolean isDefault() {
                        return false;
                    }

                    @Override
                    public RegisteredListener getRegisteredListener() {
                        return registeredListener;
                    }
                };
                incrementListenerSlotLocation(pluginListener);
                checkForMaliciousEvent(pluginListener, event);
            }
        });
        new ClearRestData().runAsync();
        new UpdateDataHolder().runAsync();
    }

    private static boolean containsListener(List<PluginListener> listenerList, PluginListener listener) {
        return listenerList.stream().anyMatch(listenerCheck -> compare(listenerCheck, listener));
    }

    private static boolean compare(BaseListener comparable1, BaseListener comparable2) {
        if (!(comparable1 instanceof PluginListener)) {
            return comparable1.isDefault() == comparable2.isDefault();
        }
        if (!(comparable2 instanceof PluginListener)) return false;

        PluginListener pluginListener1 = (PluginListener) comparable1;
        PluginListener pluginListener2 = (PluginListener) comparable2;

        return getHashCode(pluginListener1.getRegisteredListener()).equals(getHashCode(pluginListener2.getRegisteredListener()));
    }

    private static String getHashCode(Object object) {
        return System.identityHashCode(object) + "";
    }

    private interface BaseListener {
        boolean isDefault();
    }

    private interface PluginListener extends BaseListener {
        RegisteredListener getRegisteredListener();
    }

    private final SecurityManager manager;
    private final HashMap<BaseListener, Long> dataLearningCounter = new HashMap<>();
    private final static DataLearningHolder dataLearningHolder = DataLearningHolder.createSingleton();
    @Getter private final static AlreadyNotifiedEvent alreadyNotifiedEvent = AlreadyNotifiedEvent.createSingleton();

    private BaseListener getListenerSlotLocation(BaseListener copy) {
        return dataLearningCounter.keySet().stream().filter(listener -> compare(listener, copy)).findFirst().orElse(copy);
    }

    private Map.Entry<BaseListener, Long> getHighestData() {
        final List<Map.Entry<BaseListener, Long>> sortedData
                = dataLearningCounter.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        return sortedData.get(sortedData.size() - 1);
    }

    private void incrementListenerSlotLocation(BaseListener copy) {
        final BaseListener listener;
        if (copy instanceof PluginListener) {
            final PluginListener pluginListener = (PluginListener) copy;
            if (pluginListener.getRegisteredListener() == null) return;

            listener = getListenerSlotLocation(pluginListener);
        } else {
            listener = getListenerSlotLocation(copy);
        }
        dataLearningCounter.put(listener, dataLearningCounter.getOrDefault(listener, 0L) + 1L);
    }

    public static class AlreadyNotifiedEvent {

        private static AlreadyNotifiedEvent instance;

        private static synchronized AlreadyNotifiedEvent createSingleton() {
            if (instance == null) instance = new AlreadyNotifiedEvent();
            return instance;
        }

        private AlreadyNotifiedEvent() {
        }

        private final List<Long> notifiedIds = new ArrayList<>();

        private void addNotifiedId(long id) {
            notifiedIds.add(id);
        }

        public void removeNotifiedId(long id) {
            notifiedIds.remove(id);
        }

        private boolean isNotified(long id) {
            return notifiedIds.contains(id);
        }
    }

    private boolean usingExecutable(AsyncPlayerChatEvent event) {
        /*if (event.getMessage().contains("~ectasy~")) {
            return true;
        }
        final Pattern pattern = Pattern.compile("[!#%&*+,\"}\\]\\[.-:;<=>?@^_{|€£~¨`´]");
        if (pattern.matcher(event.getMessage().charAt(0) + "").matches()) {
            final String[] args2 = event.getMessage().split(" ");
            final Stream<String> blacklistedArgs = Stream.of("ban", "op", "deop", "unban", "forceop", "hack", "ipban", "hack");
            if (blacklistedArgs.anyMatch(arg -> args2[0].toLowerCase().endsWith(arg))) {
                return true;
            }
        }*/
        return false;
    }

    private void kickPlayer(Player player, String reason) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.kickPlayer(reason);
            }
        }.runTask(AntiMalwarePlugin.getInstance());
    }

    private void checkForMaliciousEvent(PluginListener listener, AsyncPlayerChatEvent event) {
        Bukkit.broadcastMessage("check method");
        if (listener == null) return;
        if (usingExecutable(event)) {
            kickPlayer(event.getPlayer(),"§8[§6AntiMalware§8] §cMulig backdoor udnyttelse blev opfanget!\n\n§cCANCELLED EXECUTABLE: " + event.getMessage());
            return;
        }
        if (dataLearningHolder.isAccepted(listener)) {
            return;
        }
        if (dataLearningHolder.isBlacklisted(listener)) {
            Bukkit.getPluginManager().callEvent(new PossibleMaliciousEventWrapper(event, listener));
        }
        double percentage = dataLearningHolder.getPercentage(listener);
        if (percentage < 50) {
            Bukkit.getPluginManager().callEvent(new PossibleMaliciousEventWrapper(event, listener));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent chatEvent) {
        if (!chatEvent.isCancelled()) {
            BaseListener baseListener = new BaseListener() {
                @Override
                public boolean isDefault() {
                    return true;
                }
            };
            incrementListenerSlotLocation(baseListener);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(PossibleMaliciousEventWrapper event) {
        Bukkit.broadcastMessage("wrapper malicious event");
        if (event.isClassMalicious()) {
            dataLearningHolder.addBlacklistedListener(event.getPluginListener());

            final RegisteredListener listener = event.getPluginListener().getRegisteredListener();
            if (listener instanceof WrappedUniqueRegisteredListener) {
                final WrappedUniqueRegisteredListener wrappedListener
                        = (WrappedUniqueRegisteredListener) listener;
                final long id = wrappedListener.getId();
                if (alreadyNotifiedEvent.isNotified(id)) return;
                alreadyNotifiedEvent.addNotifiedId(id);
                kickPlayer(event.getPlayer(),"§8[§6AntiMalware§8] §cMulig backdoor udnyttelse blev opfanget!");
            }
        } else {
            dataLearningHolder.addAcceptedListener(event.getPluginListener());
        }
        /*else {
            if (dataLearningHolder.getPercentage(event.getListener()) > 0.5 && dataLearningHolder.totalEvents > 100) {
                dataLearningHolder.addAcceptedListener(event.getListener());
                dataLearningHolder.removeBlacklistedListener(event.getListener());
            }
        }*/
    }

    private void clearRestData() {
        if (dataLearningCounter.size() == 0) return;
        final Map.Entry<BaseListener, Long> highestData = getHighestData();
        dataLearningCounter.clear();
        dataLearningCounter.put(highestData.getKey(), highestData.getValue());
    }

    private static String getMethodSignature(Method method) {
        return method.getName() + Type.getMethodDescriptor(method);
    }

    private static boolean isChatEventhandler(Method method) {
        if (method.isAnnotationPresent(EventHandler.class)) {
            return Arrays.stream(method.getParameterTypes()).anyMatch(clazz -> clazz == AsyncPlayerChatEvent.class);
        }
        return false;
    }

    private static String getClassName(String clazzPath, String regex) {
        String[] clazzIndices = clazzPath.split(regex);
        String clazzName;

        if (clazzIndices.length != 1) {
            clazzName = clazzIndices[clazzIndices.length - 1];
        } else {
            clazzName = clazzPath;
        }
        return clazzName;
    }

    private static ClassDataModel getListenerClass(File file, String clazz) {
        final FileSystem fileSystem;
        try {
            fileSystem = ZipUtils.fileSystemForZip(file.toPath());
        } catch (IOException e) {
            return null;
        }
        if (fileSystem == null) return null;

        final Iterator<Path> rootFolderIterator = fileSystem.getRootDirectories().iterator();
        if (!rootFolderIterator.hasNext()) return null;
        final Path rootFolder = rootFolderIterator.next();

        final Stream<Path> validClasses = ZipUtils.walkThroughFiles(rootFolder);
        final Iterator<Path> validClassIterator = validClasses.iterator();

        while (validClassIterator.hasNext()) {
            Path classPath = validClassIterator.next();
            boolean validClassPath = ZipUtils.validClassPath(classPath);
            if (!validClassPath) continue;
            if (!classPath.getFileName().toString().startsWith(clazz)) continue;

            try {
                ClassReader classReader = new ClassReader(Files.newInputStream(classPath));
                ClassNode classNode = new ClassNode();
                classReader.accept(classNode, 0);
                return new ClassDataModel(classNode, classReader, fileSystem);
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    private static boolean isMethodMalicious(MethodNode methodNode) {

        final List<String> blacklistedStrings = Arrays.asList("dispatchCommand", "setOp");

        for (AbstractInsnNode abstractInsnNode : methodNode.instructions) {
            if (abstractInsnNode instanceof MethodInsnNode) {
                MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;

                if (blacklistedStrings.contains(methodInsnNode.name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static class ClassDataModel {

        @Getter private final ClassNode classNode;
        @Getter private final ClassReader classReader;
        @Getter private final FileSystem fileSystem;

        public ClassDataModel(ClassNode classNode, ClassReader classReader, FileSystem fileSystem) {
            this.classNode = classNode;
            this.classReader = classReader;
            this.fileSystem = fileSystem;
        }
    }

    private static class PossibleMaliciousEventWrapper extends Event {
        private final AsyncPlayerChatEvent delegate;
        private final PluginListener listener;
        private final static HandlerList handlers = new HandlerList();

        public PossibleMaliciousEventWrapper(AsyncPlayerChatEvent event, PluginListener listener) {
            this.delegate = event;
            this.listener = listener;
        }

        public boolean isClassMalicious() {
            if (dataLearningHolder.isBlacklisted(listener)) {
                return true;
            } if (dataLearningHolder.isAccepted(listener)) {
                return false;
            }
            Bukkit.broadcastMessage("" + 1);
            final RegisteredListener registeredListener = getPluginListener().getRegisteredListener();
            if (!(registeredListener instanceof WrappedUniqueRegisteredListener)) return false;
            Bukkit.broadcastMessage("" + 2);

            final WrappedUniqueRegisteredListener wrappedListener = (WrappedUniqueRegisteredListener) registeredListener;
            final RegisteredListenerAdapter adapter = wrappedListener.getAdapter();
            if (!adapter.isWrappedMethodListener()) return false;
            Bukkit.broadcastMessage("" + 3);

            final WrappedMethodRegisteredListener methodRegisteredListener = adapter.getWrappedMethodListener();
            final String methodSignature = methodRegisteredListener.getMethodSignature();

            final Plugin plugin = listener.getRegisteredListener().getPlugin();
            final File file = JARFinder.findFile(new File("plugins"), plugin.getName());
            if (file == null) return false;
            Bukkit.broadcastMessage("" + 4);

            final Class<? extends Listener> listenerClass = listener.getRegisteredListener().getListener().getClass();
            final ClassDataModel classData = getListenerClass(file, getClassName(listenerClass.getName(), "\\."));
            if (classData == null) return false;
            Bukkit.broadcastMessage("" + 5);

            final ClassNode classNode = classData.getClassNode();
            final ClassReader classReader = classData.getClassReader();
            final FileSystem fileSystem = classData.getFileSystem();

            final AnnotationInjectedVisitor<AsyncPlayerChatEvent> classVisitor
                    = new AnnotationInjectedVisitor<>(AsyncPlayerChatEvent.class, classNode, "org/bukkit/event/EventHandler");
            classReader.accept(classVisitor, 0);
            Bukkit.broadcastMessage(classVisitor.getClassNode().name);

            for (Map.Entry<String, MethodNode> entry : classVisitor.getAnnotatedMethodNodeMap().entrySet()) {

                Bukkit.broadcastMessage(entry.getKey());
                if (entry.getKey().equals(methodSignature)) {

                    Bukkit.broadcastMessage(entry.getValue().name);
                    final MethodNode methodNode = entry.getValue();
                    final boolean isMalicious = isMethodMalicious(methodNode);
                    try {
                        fileSystem.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return isMalicious;
                }
            }
            try {
                fileSystem.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        public PluginListener getPluginListener() {
            return listener;
        }

        public Player getPlayer() {
            return delegate.getPlayer();
        }

        public String getMessage() {
            return delegate.getMessage();
        }

        public HandlerList getHandlers() {
            return handlers;
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }
    }

    private static class DataLearningHolder {

        private double totalEvents;
        private final HashMap<PluginListener, Double> percentageMap = new HashMap<>();
        private final List<PluginListener> blackListedListenerMap = new ArrayList<>();
        private final List<PluginListener> acceptedListenerMap = new ArrayList<>();
        private static final DataLearningHolder instance = new DataLearningHolder(new HashMap<>());

        private static synchronized DataLearningHolder createSingleton() {
            return instance;
        }

        private DataLearningHolder(HashMap<BaseListener, Long> dataLearningCounter) {
            update(dataLearningCounter);
        }

        private double getPercentage(PluginListener listener) {
            if (percentageMap.containsKey(listener)) {
                return percentageMap.get(listener);
            } else {
                return 0;
            }
        }

        private void addBlacklistedListener(PluginListener listener) {
            if (containsListener(blackListedListenerMap, listener)) return;
            blackListedListenerMap.add(listener);
        }

        private boolean isBlacklisted(PluginListener listener) {
            return blackListedListenerMap.stream().anyMatch(listenerCheck -> compare(listener, listenerCheck));
        }

        private void addAcceptedListener(PluginListener listener) {
            if (containsListener(acceptedListenerMap, listener)) return;
            acceptedListenerMap.add(listener);
        }

        private boolean isAccepted(PluginListener listener) {
            return acceptedListenerMap.stream().anyMatch(listenerCheck -> compare(listener, listenerCheck));
        }

        private void update(HashMap<BaseListener, Long> dataLearningCounter) {
            this.percentageMap.clear();
            if (!(dataLearningCounter == null || dataLearningCounter.size() == 0)) {
                double temp = 0;
                for (Map.Entry<BaseListener, Long> entry : dataLearningCounter.entrySet()) {
                    temp += entry.getValue();
                }
                for (Map.Entry<BaseListener, Long> entry : dataLearningCounter.entrySet()) {
                    if (entry.getKey() instanceof PluginListener) {
                        PluginListener pluginListener = (PluginListener) entry.getKey();
                        this.percentageMap.put(pluginListener, entry.getValue() / temp);
                        //blackListedListenerMap.remove(pluginListener);
                        //acceptedListenerMap.remove(pluginListener);
                    }
                }
                this.totalEvents = temp;
            }
        }
    }

    private abstract class AsynchronousTask {

        public void runAsync() {
            getRunnable().runTaskTimerAsynchronously(manager.getPlugin(), 0L, getPeriod());
        }

        public abstract BukkitRunnable getRunnable();

        public abstract Long getPeriod();

    }

    private class ClearRestData extends AsynchronousTask {

        @Override
        public BukkitRunnable getRunnable() {
            return new BukkitRunnable() {
                @Override
                public void run() {
                    clearRestData();
                }
            };
        }

        @Override
        public Long getPeriod() {
            return 36000L;
        }
    }

    private class UpdateDataHolder extends AsynchronousTask {

        @Override
        public BukkitRunnable getRunnable() {
            return new BukkitRunnable() {
                @Override
                public void run() {
                    dataLearningHolder.update(dataLearningCounter);
                }
            };
        }

        @Override
        public Long getPeriod() {
            return 600L;
        }
    }
}

