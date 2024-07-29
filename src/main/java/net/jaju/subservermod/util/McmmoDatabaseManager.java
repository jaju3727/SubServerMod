package net.jaju.subservermod.util;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class McmmoDatabaseManager {

    private static final String FILE_PATH = "plugins/mcMMO/flatfile/mcmmo.users";

    public static Map<String, String[]> loadMcMMOUserData() {
        Map<String, String[]> userData = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#")) { // Skip comment lines
                    String[] parts = line.split(":");
                    if (parts.length > 1) {
                        String username = parts[0];
                        String[] data = new String[parts.length - 1];
                        System.arraycopy(parts, 1, data, 0, parts.length - 1);
                        userData.put(username, data);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userData;
    }

    public static void sendParsedUserDataToPlayer(Map<String, String[]> userData, LocalPlayer player) {
        for (Map.Entry<String, String[]> entry : userData.entrySet()) {
            String username = entry.getKey();
            String[] data = entry.getValue();

            player.sendSystemMessage(Component.literal("플레이어 이름 (USERNAME_INDEX = 0): " + username));
            player.sendSystemMessage(Component.literal("스킬 레벨: 채광 (SKILLS_MINING = 1): " + data[0]));
            player.sendSystemMessage(Component.literal("무시된 필드: IGNORED (두 개)"));
            player.sendSystemMessage(Component.literal("경험치: 채광 (EXP_MINING = 4): " + data[3]));
            player.sendSystemMessage(Component.literal("스킬 레벨: 벌목 (SKILLS_WOODCUTTING = 5): " + data[4]));
            player.sendSystemMessage(Component.literal("경험치: 벌목 (EXP_WOODCUTTING = 6): " + data[5]));
            player.sendSystemMessage(Component.literal("스킬 레벨: 수리 (SKILLS_REPAIR = 7): " + data[6]));
            player.sendSystemMessage(Component.literal("스킬 레벨: 맨손 전투 (SKILLS_UNARMED = 8): " + data[7]));
            player.sendSystemMessage(Component.literal("스킬 레벨: 약초학 (SKILLS_HERBALISM = 9): " + data[8]));
            player.sendSystemMessage(Component.literal("스킬 레벨: 발굴 (SKILLS_EXCAVATION = 10): " + data[9]));
            player.sendSystemMessage(Component.literal("스킬 레벨: 궁술 (SKILLS_ARCHERY = 11): " + data[10]));
            player.sendSystemMessage(Component.literal("스킬 레벨: 검술 (SKILLS_SWORDS = 12): " + data[11]));
            player.sendSystemMessage(Component.literal("스킬 레벨: 도끼 (SKILLS_AXES = 13): " + data[12]));
            player.sendSystemMessage(Component.literal("스킬 레벨: 곡예 (SKILLS_ACROBATICS = 14): " + data[13]));
            player.sendSystemMessage(Component.literal("경험치: 수리 (EXP_REPAIR = 15): " + data[14]));
            player.sendSystemMessage(Component.literal("경험치: 맨손 전투 (EXP_UNARMED = 16): " + data[15]));
            player.sendSystemMessage(Component.literal("경험치: 약초학 (EXP_HERBALISM = 17): " + data[16]));
            player.sendSystemMessage(Component.literal("경험치: 발굴 (EXP_EXCAVATION = 18): " + data[17]));
            player.sendSystemMessage(Component.literal("경험치: 궁술 (EXP_ARCHERY = 19): " + data[18]));
            player.sendSystemMessage(Component.literal("경험치: 검술 (EXP_SWORDS = 20): " + data[19]));
            player.sendSystemMessage(Component.literal("경험치: 도끼 (EXP_AXES = 21): " + data[20]));
            player.sendSystemMessage(Component.literal("경험치: 곡예 (EXP_ACROBATICS = 22): " + data[21]));
            player.sendSystemMessage(Component.literal("무시된 필드: IGNORED"));
            player.sendSystemMessage(Component.literal("스킬 레벨: 길들이기 (SKILLS_TAMING = 24): " + data[23]));
            player.sendSystemMessage(Component.literal("경험치: 길들이기 (EXP_TAMING = 25): " + data[24]));
            player.sendSystemMessage(Component.literal("쿨다운: 광폭화 (COOLDOWN_BERSERK = 26): " + data[25]));
            player.sendSystemMessage(Component.literal("쿨다운: 기가 드릴 브레이커 (COOLDOWN_GIGA_DRILL_BREAKER = 27): " + data[26]));
            player.sendSystemMessage(Component.literal("쿨다운: 나무 쓰러뜨리기 (COOLDOWN_TREE_FELLER = 28): " + data[27]));
            player.sendSystemMessage(Component.literal("쿨다운: 그린 테라 (COOLDOWN_GREEN_TERRA = 29): " + data[28]));
            player.sendSystemMessage(Component.literal("쿨다운: 톱니 모양 스트라이크 (COOLDOWN_SERRATED_STRIKES = 30): " + data[29]));
            player.sendSystemMessage(Component.literal("쿨다운: 두개골 분쇄기 (COOLDOWN_SKULL_SPLITTER = 31): " + data[30]));
            player.sendSystemMessage(Component.literal("쿨다운: 슈퍼 브레이커 (COOLDOWN_SUPER_BREAKER = 32): " + data[31]));
            player.sendSystemMessage(Component.literal("무시된 필드: IGNORED"));
            player.sendSystemMessage(Component.literal("스킬 레벨: 낚시 (SKILLS_FISHING = 34): " + data[33]));
            player.sendSystemMessage(Component.literal("경험치: 낚시 (EXP_FISHING = 35): " + data[34]));
            player.sendSystemMessage(Component.literal("쿨다운: 발파 광업 (COOLDOWN_BLAST_MINING = 36): " + data[35]));
            player.sendSystemMessage(Component.literal("무시된 필드: IGNORED (세 개)"));
            player.sendSystemMessage(Component.literal("UUID (UUID_INDEX = 41): " + data[40]));
            player.sendSystemMessage(Component.literal("무시된 필드: 0 (두 개)"));
            player.sendSystemMessage(Component.literal("마지막 로그인 시간 (OVERHAUL_LAST_LOGIN = 44): " + data[43]));
            player.sendSystemMessage(Component.literal("무시된 필드: 0 (여러 개)"));
            player.sendSystemMessage(Component.literal(""));
        }
    }
}
