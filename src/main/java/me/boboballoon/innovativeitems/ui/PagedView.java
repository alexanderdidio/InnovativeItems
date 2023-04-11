package me.boboballoon.innovativeitems.ui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a paged inventory view ui
 */
public class PagedView extends PageableView {
    /**
     * Creates a new instance of PagedView (it is assumed that the size of all elements of pages are the same size)
     *
     * @param title the title of the inventory
     * @param pages all pages
     * @throws IndexOutOfBoundsException if the pages list has a size of 0 or not all of its elements have the same size
     */
    public PagedView(@NotNull String title, @NotNull List<List<InnovativeElement>> pages) throws IndexOutOfBoundsException {
        super(title, pages);
        this.setPages(this.reformat(pages));
    }

    public PagedView(@NotNull String title, @NotNull List<InnovativeElement>[] pages) throws IndexOutOfBoundsException {
        this(title, Lists.newArrayList(pages));
    }

    /**
     * Converts types and adds in arrow elements in top row
     *
     * @param pages the pages of the view
     * @return the completed page collection
     */
    @NotNull
    private List<ImmutableList<InnovativeElement>> reformat(@NotNull List<List<InnovativeElement>> pages) {
        List<ImmutableList<InnovativeElement>> elements = new ArrayList<>(pages.size());

        BlankElement previous = new BlankElement(new ItemStack(Material.ARROW), player -> this.setCurrentPageIndex(this.getCurrentPageIndex() - 1));
        BlankElement next = new BlankElement(new ItemStack(Material.ARROW), player -> this.setCurrentPageIndex(this.getCurrentPageIndex() + 1));

        for (int i = 0; i < pages.size(); i++) {
            List<InnovativeElement> page = pages.get(i);

            if (i > 0) {
                page.set(0, previous); //first slot in first row
            }

            if (i < pages.size() - 1) {
                page.set(8, next); //last slot in first row
            }

            elements.add(ImmutableList.copyOf(page));
        }

        return elements;
    }
}